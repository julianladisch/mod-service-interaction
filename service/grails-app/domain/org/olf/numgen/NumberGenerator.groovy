package org.olf.numgen;

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

class NumberGenerator implements MultiTenant<NumberGenerator> {

  String id
  String code
  String name
  static hasMany = [ sequences: NumberGeneratorSequence ]

  static mapping = {

           id column: 'ng_id', generator: 'uuid2', length:36
      version column: 'ng_version'
         code column: 'ng_code'
         name column: 'ng_name'

    sequences cascade: 'all-delete-orphan'
  }

}
