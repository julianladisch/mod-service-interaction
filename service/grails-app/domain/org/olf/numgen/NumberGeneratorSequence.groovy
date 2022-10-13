package org.olf.numgen;

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

class NumberGeneratorSequence implements MultiTenant<NumberGeneratorSequence> {

  String id
  NumberGenerator owner
  String code
  String name
  String prefix
  String postfix
  String format
  Long nextValue
  String outputTemplate
  String description
  Boolean enabled = Boolean.TRUE

  @Defaults(['None', 'EAN13', 'Modulo10', 'Modulo11', 'Modulo16', 'Modulo43', 'Modulo47'])
  RefdataValue checkDigitAlgo

  static constraints = {
            prefix(nullable: true)
           postfix(nullable: true)
         nextValue(nullable: true)
            format(nullable: true)
    checkDigitAlgo(nullable: true)
    outputTemplate(nullable: true)
       description(nullable: true)
           enabled(nullable: true)
  }


  static mapping = {
                id column: 'ngs_id', generator: 'uuid2', length:36
           version column: 'ngs_version'
             owner column: 'ngs_owner'
              code column: 'ngs_code'
              name column: 'ngs_name'
            prefix column: 'ngs_prefix'
           postfix column: 'ngs_postfix'
         nextValue column: 'ngs_next_value'
            format column: 'ngs_format'
    checkDigitAlgo column: 'ngs_check_digit_algorithm'
    outputTemplate column: 'ngs_output_template'
       description column: 'ngs_description'
           enabled column: 'ngs_enabled'
  }

  public String toString() {
    return "NumberGeneratorSequence(${owner?.code}.${code} ${prefix} ${nextValue} ${postfix} ${format} ${checkDigitAlgo?.value},${outputTemplate})".toString();
  }
}
