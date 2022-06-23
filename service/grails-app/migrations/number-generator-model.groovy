databaseChangeLog = {

  changeSet(author: "ianibbo (manual)", id: "2022-05-02-1007-001") {

    createTable(tableName: "number_generator") {
      column(name: "ng_id", type: "VARCHAR(36)") { constraints(nullable: "false") }
      column(name: "ng_version", type: "BIGINT") { constraints(nullable: "false") }
      column(name: "ng_code", type: "VARCHAR(36)") { constraints(nullable: "false") }
      column(name: "ng_name", type: "VARCHAR(100)") { constraints(nullable: "false") }
    }

    addPrimaryKey(columnNames: "ng_id", constraintName: "NumberGeneratorPK", tableName: "number_generator")

    addUniqueConstraint(columnNames: "ng_code", constraintName: "NumberGeneratorUniqueCode", tableName: "number_generator")

    createTable(tableName: "number_generator_sequence") { 
      column(name: "ngs_id", type: "VARCHAR(36)") { constraints(nullable: "false") }
      column(name: "ngs_version", type: "BIGINT") { constraints(nullable: "false") }
      column(name: "ngs_owner", type: "VARCHAR(36)") { constraints(nullable: "false") }
      column(name: "ngs_code", type: "VARCHAR(36)") { constraints(nullable: "false") }
      column(name: "ngs_prefix", type: "VARCHAR(100)")
      column(name: "ngs_postfix", type: "VARCHAR(100)")
      column(name: "ngs_next_value", type: "BIGINT")
      column(name: "ngs_format", type: "VARCHAR(20)")
    }

    addPrimaryKey(columnNames: "ngs_id", constraintName: "NumberGeneratorSequencePK", tableName: "number_generator_sequence")

    addUniqueConstraint(columnNames: "ngs_owner,ngs_code", constraintName: "NumberGeneratorSequenceUniqueCode", tableName: "number_generator_sequence")

    addForeignKeyConstraint(baseColumnNames: "ngs_owner",
      baseTableName: "number_generator_sequence",
      constraintName: "number_generator_sequence_owner_fk",
      deferrable: "false", initiallyDeferred: "false",
      referencedColumnNames: "ng_id", referencedTableName: "number_generator")

  
  }

  changeSet(author: "ianibbo (manual)", id: "2022-06-15-0811-001") {
    addColumn(tableName: "number_generator_sequence") {
      column(name: "ngs_check_digit_algorithm", type: "VARCHAR(36)")
      column(name: "ngs_output_template", type: "VARCHAR(256)")
    }
  }

}
