package org.olf.numgen;

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

class NumberGeneratorSequence implements MultiTenant<NumberGeneratorSequence> {

  String id
  NumberGenerator owner
  String code
  String prefix
  String postfix
  String format
  Long nextValue

  static constraints = {
       prefix(nullable: true)
      postfix(nullable: true)
    nextValue(nullable: true)
       format(nullable: true)
  }


  static mapping = {
           id column: 'ngs_id', generator: 'uuid2', length:36
      version column: 'ngs_version'
        owner column: 'ngs_owner'
         code column: 'ngs_code'
       prefix column: 'ngs_prefix'
      postfix column: 'ngs_postfix'
    nextValue column: 'ngs_next_value'
       format column: 'ngs_format'
  }

  public String toString() {
    return "NumberGeneratorSequence(${owner?.code}.${code} ${prefix} ${nextValue} ${postfix} ${format})".toString();
  }
}
