import java.sql.{Connection, DriverManager, Statement}

import org.scalatest.{BeforeAndAfterAll, FunSuite}

class JdbcTest extends FunSuite with BeforeAndAfterAll {
  val PARENT_DIR:String = "./data-dir"
  val DATABASE_NAME: String = "my-h2-db" // it's better if you write db name in small letters
  val DATABASE_DIR: String = s"$PARENT_DIR/$DATABASE_NAME" // FYI, this is string interpolation
  val DATABASE_URL: String = s"jdbc:h2:$DATABASE_DIR"

  test("test H2 embedded database") {
    var row1InsertionCheck = false
    val con: Connection = DriverManager.getConnection(DATABASE_URL)
    val stm: Statement = con.createStatement
    val sql: String =
      """
        |create table test_table1(ID INT PRIMARY KEY,NAME VARCHAR(500));
        |insert into test_table1 values (1,'A');""".stripMargin

    val sql2 =
      """
        |insert into test_table1 values (2,'b'),(3,'c');
        |""".stripMargin

    stm.execute(sql)
    val rs = stm.executeQuery("select * from test_table1")

    val stm2 = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)
    val bb= stm2.executeUpdate()

    val rx = stm2.getGeneratedKeys()
    rx.next()
    val aa =rx.getInt("ID")

    rs.next
    row1InsertionCheck = (1 == rs.getInt("ID")) && ("A" == rs.getString("NAME"))

    assert(row1InsertionCheck, "Data not inserted")
  }
}
