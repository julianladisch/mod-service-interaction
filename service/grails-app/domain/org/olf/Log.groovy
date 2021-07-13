package org.olf

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue


class Log implements MultiTenant<Log> {
  String id
  String message

  static mapping = {
         id column: 'log_id', generator: 'uuid2', length:36
    version column: 'log_version'
    message column: 'log_message'
  }

}
