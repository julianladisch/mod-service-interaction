databaseChangeLog = {
  changeSet(author: "efreestone (manual)", id: "2021-07-13-1050-001") {
    createTable(tableName: "log") {
      column(name: "log_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "log_version", type: "BIGINT") {
        constraints(nullable: "false")
      }
      column(name: "log_message", type: "text")
    }
  }
}