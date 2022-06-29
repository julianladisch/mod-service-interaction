package org.olf.numgen;

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

class NumberGenerator implements MultiTenant<NumberGenerator> {

  String id
  String code
  String name

  // If set, this can identify a sepecific sequence to use when users generate a number
  // using this generator but do not specify a sequence
  String defaultSequenceCode

  static hasMany = [ sequences: NumberGeneratorSequence ]

  static mapping = {

                     id column: 'ng_id', generator: 'uuid2', length:36
                version column: 'ng_version'
                   code column: 'ng_code'
    defaultSequenceCode column: 'ng_default_seq_code'
                   name column: 'ng_name'

             sequences cascade: 'all-delete-orphan'
  }

  static constraints = {
        defaultSequenceCode(nullable: true)
  }

}
