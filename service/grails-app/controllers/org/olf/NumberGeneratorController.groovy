package org.olf

import grails.rest.*
import grails.converters.*
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.numgen.NumberGenerator
import org.olf.numgen.NumberGeneratorSequence
import java.text.DecimalFormat 

@Slf4j
@CurrentTenant
class NumberGeneratorController extends OkapiTenantAwareController<NumberGeneratorController> {

  NumberGeneratorController() {
    super(NumberGenerator)
  }

  public getNextNumber(String generator, String sequence) {
    log.debug("NumberGeneratorController::getNextNumber(${generator},${sequence})");
    Map result = [:]
    NumberGenerator.withTransaction { status ->
      NumberGeneratorSequence ngs = NumberGeneratorSequence.createCriteria().get { 
        owner {
          eq('code', generator)
        }
        eq('code', sequence) 
        lock true
      }

      log.debug("Got seq : ${ngs}");

      Long next_seqno = null;

      if ( ngs != null  ) {
        if ( ngs.nextValue == null ) {
          next_seqno=0
          ngs.nextValue = 1
        }
        else {
          next_seqno=ngs.nextValue++
        }

        if ( next_seqno != null ) {

          String value_without_checksum = null;

          if ( ngs.format != null) {
            DecimalFormat df = new DecimalFormat(ngs.format)
            value_without_checksum = "${ngs.prefix?:''}${df.format(next_seqno)}${ngs.postfix?:''}"
          }
          else {
            value_without_checksum = "${ngs.prefix?:''}${next_seqno}${ngs.postfix?:''}"
          }

          String checksum = null;
          if ( ngs.checkDigitAlgo != null ) {
            checksum = generateCheckSum(ngs.checkDigitAlgo.value, value_without_checksum)
          }

          result.nextValue = "${value_without_checksum}${checksum?:''}".toString();
        }

        ngs.save(flush:true, failOnError:true);
      }
      else {
        result.status = 'ERROR'
        result.message = "unable to locate NumberGeneratorSequence for ${generator}.${sequence}".toString()
      }
    }

    render result as JSON;
  }

  // See https://www.activebarcode.com/codes/checkdigit/modulo47.html
  private String generateCheckSum(String algorithm, String checksum) {
    log.debug("generateCheckSum(${algorithm},${checksum})");
    return null;
  }
}
