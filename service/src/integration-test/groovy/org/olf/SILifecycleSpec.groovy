package org.olf

import static groovyx.net.http.ContentTypes.*
import static groovyx.net.http.HttpBuilder.configure
import static org.springframework.http.HttpStatus.*

import com.k_int.okapi.OkapiHeaders
import com.k_int.okapi.OkapiTenantResolver
import geb.spock.GebSpec
import grails.gorm.multitenancy.Tenants
import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import groovyx.net.http.ChainedHttpConfig
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import groovyx.net.http.HttpVerb
import java.time.LocalDate
import spock.lang.Stepwise
import spock.lang.Unroll

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile

import groovy.util.logging.Slf4j

@Slf4j
@Integration
@Stepwise
class SILifecycleSpec extends BaseSpec {

  void "Configure Number Generator" () {

    when: 'We post the user barcode number generator'
      log.debug("Create new number generator for user barcode")

      Map user_barcode_numgen = [
        'code': 'TSTUserBarcode',
        'name': 'TSTUser Barcode',
        'sequences':[
          [ 'code':'patron',    'prefix':'user',   postfix:null,    format:'000000000' ],
          [ 'code':'staff',     'prefix':'staff',  postfix:'test', format:'000,000,000' ],
          [ 'code':'noformat',  'prefix':'nf' ],
          [ 'code':'highinit',  'prefix':'hi', 'format':'000000000', 'nextValue':100000 ],
          [ 'code':'mod10test', 'format':'000000000', 'nextValue':100000, 'checkDigitAlgo':'Modulo10' ],
          [ 'code':'mod11test', 'format':'000000000', 'nextValue':100000, 'checkDigitAlgo':'Modulo11' ],
          [ 'code':'mod16test', 'format':'000000000', 'nextValue':100000, 'checkDigitAlgo':'Modulo16' ],
          [ 'code':'mod43test', 'format':'000000000', 'nextValue':100000, 'checkDigitAlgo':'Modulo43' ],
          [ 'code':'mod47test', 'format':'000000000', 'nextValue':100000, 'checkDigitAlgo':'Modulo47' ],
          [ 'code':'069',       'prefix':'069', 'postfix':'1', 'format':'000000000', 'nextValue':1, 'checkDigitAlgo':'EAN13' ],
          [ 'code':'0698',      'format':'000000000', 'nextValue':1, 'checkDigitAlgo':'EAN13', 'outputTemplate':'0698${generated_number}${checksum}' ],
          [ 'code':'DD',        'prefix':'DD',   'format':'000000000', 'nextValue':1 ]
        ]
      ]

      Map respMap = doPost("/servint/numberGenerators", user_barcode_numgen)

    then: "Response is good and we have a new ID"
      respMap.id != null
  }

  void "Get next number in user patron sequence"(gen, seq, expected_response_code, expected_result) {
    when: 'We post to the getNextNumber action'
      Map resp = doGet("/servint/numberGenerators/getNextNumber", ['generator':gen, 'sequence':seq] )
    then: 'We get the next number'
      log.debug("Got result ${resp}");
      resp != null;
      resp.nextValue == expected_result
    where:
      gen | seq | expected_response_code | expected_result
      'TSTUserBarcode' | 'patron'    | 200 | 'user-000000000'
      'TSTUserBarcode' | 'patron'    | 200 | 'user-000000001'
      'TSTUserBarcode' | 'patron'    | 200 | 'user-000000002'
      'TSTUserBarcode' | 'staff'     | 200 | 'staff-000,000,000-test'
      'TSTUserBarcode' | 'noformat'  | 200 | 'nf-0'
      'TSTUserBarcode' | 'highinit'  | 200 | 'hi-000100000'
      'TSTUserBarcode' | 'mod10test' | 200 | '000100000'
      'TSTUserBarcode' | '069'       | 200 | '069-000000001-1-7'
      'TSTUserBarcode' | '0698'      | 200 | '06980000000017'
      'TSTUserBarcode' | 'DD'        | 200 | 'DD-000000001'
  }

  void "Get Number Generator Record"() {
    when: 'we get the UserBarcode generator'
      Map resp = doGet("/servint/numberGenerators", [filters:['code==TSTUserBarcode'], stats:'true'])

    then: 'Get the record back'
      log.debug("Got resp ${resp}");
      resp != null
      resp.totalRecords == 1
  }
}

