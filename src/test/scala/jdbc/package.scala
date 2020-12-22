package object jdbc {
  case class H2DbInfo(parentDir:String, dbName: String) {
    val dir = f"$parentDir/$dbName"
    val url = s"jdbc:h2:$dir"
  }

  case class H2TableInfo(h2DbInfo: H2DbInfo, tableName: String, schema: String) {
    val createDDL = f"CREATE TABLE $tableName($schema);"
    val dropDDL   = f"DROP TABLE IF EXISTS $tableName;"
    def insertQuery(values: Seq[String]) = f"INSERT INTO $tableName VALUES ${values.mkString(",")}"
    val selectAllQuery = f"SELECT * FROM $tableName"
  }

  val defaultTestDb: H2DbInfo = H2DbInfo("./test-dbs", "jdbc-test-h2-db")

  val testExecuteTable: H2TableInfo = H2TableInfo(defaultTestDb, "test_execute_table", "ID INT PRIMARY KEY")
  val testExecuteUpdateTable: H2TableInfo = H2TableInfo(defaultTestDb, "test_execute_update_table", "ID INT PRIMARY KEY, NAME VARCHAR(500)")
  val testExecuteQueryTable: H2TableInfo = H2TableInfo(defaultTestDb, "test_execute_query_table", "ID INT PRIMARY KEY, NAME VARCHAR(500)")
  val testSingleNotNullStringColumnTable: H2TableInfo = H2TableInfo(defaultTestDb, "test_single_not_null_string_column_table", "NAME VARCHAR(500)")
  val testSingleNullableStringColumnTable: H2TableInfo = H2TableInfo(defaultTestDb, "test_single_nullable_string_column_table", "NAME VARCHAR(500)")
}
