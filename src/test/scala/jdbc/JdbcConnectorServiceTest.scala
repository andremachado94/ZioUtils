package jdbc

import java.sql.SQLException

import net.amsoft.zioutils.services.jdbc.JdbcConnectorService
import net.amsoft.zioutils.services.jdbc.JdbcConnectorService.ConnectionInfo
import org.h2.jdbc.JdbcSQLException
import zio.ZIO
import zio.blocking.Blocking
import zio.test.Assertion.{anything, equalTo, fails, isSubtype}
import zio.test._

/**
 *
 */
object JdbcConnectorServiceTest {//extends  //org.scalatest.Suite {
  //override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = jdbcSuite

  val jdbcSuite: Spec[Any, TestFailure[Throwable], TestSuccess] = suite("jdbc") (
    suite("Test Base Methods")(
      suite("Test JdbcConnectorService.getConnection")(
        testM("Connect to db (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(defaultTestDb.url))
              isValid    <- ZIO.effect(connection.isValid(10))
              _          <- JdbcConnectorService.closeConnection(connection)
            } yield zio.test.assert(true)(equalTo(isValid))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        },
        testM("Connect to db (fails: no suitable driver for url)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo("jdbc:hxx:invalid_url")).run
            } yield zio.test.assert(connection)(fails(isSubtype[SQLException](anything)))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        }
      ),
      suite("Test JdbcConnectorService.closeConnection")(
        testM("Close connection to db (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(defaultTestDb.url))
              _          <- JdbcConnectorService.closeConnection(connection)
              isValid    <- ZIO.effect(connection.isValid(10))
            } yield zio.test.assert(false)(equalTo(isValid))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        },
        testM("Close connection to db if already closed (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(defaultTestDb.url))
              pipe       <- JdbcConnectorService.closeConnection(connection).fork
              _          <- pipe.join // Make sure this executed before running closeConnection again
              _          <- JdbcConnectorService.closeConnection(connection).run
              isValid    <- ZIO.effect(connection.isValid(10))
            } yield zio.test.assert(false)(equalTo(isValid))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        }
      ),
      suite("Test JdbcConnectorService.execute")(
        testM("Create test table (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(testExecuteTable.h2DbInfo.url))
              execReturn <- JdbcConnectorService.execute(connection, f"${testExecuteTable.dropDDL}${testExecuteTable.createDDL}")
              _          <- JdbcConnectorService.closeConnection(connection)
              //TODO proper assertion to check if table was created
            } yield zio.test.assert(execReturn)(equalTo(false))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        },
        testM("Create test table (fails: already exists)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(testExecuteTable.h2DbInfo.url))
              execReturn <- JdbcConnectorService.execute(connection, f"${testExecuteTable.dropDDL}${testExecuteTable.createDDL}").fork
              _          <- execReturn.join
              execReturn <- JdbcConnectorService.execute(connection, f"${testExecuteTable.createDDL}").run
              _          <- JdbcConnectorService.closeConnection(connection)
            } yield zio.test.assert(execReturn)(fails(isSubtype[JdbcSQLException](anything)))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        }
      ),
      suite("Test JdbcConnectorService.executeUpdate")(
        testM("Insert Rows (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(testExecuteUpdateTable.h2DbInfo.url))
              _          <- JdbcConnectorService.execute(connection, f"${testExecuteUpdateTable.dropDDL}${testExecuteUpdateTable.createDDL}")
              execReturn <- JdbcConnectorService.executeUpdate(connection, testExecuteUpdateTable.insertQuery(Seq("(1,'Andre')","(2,'Joao')","(3,'Maria')")))
              _          <- JdbcConnectorService.closeConnection(connection)
            } yield zio.test.assert(execReturn)(equalTo(3))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        }
      ),
      suite("Test JdbcConnectorService.executeQuery")(
        testM("Extract ResultSet (success)") {
          val test_case =
            for {
              connection <- JdbcConnectorService.getConnection(ConnectionInfo(testExecuteQueryTable.h2DbInfo.url))
              _          <- JdbcConnectorService.execute(connection, f"${testExecuteQueryTable.dropDDL}${testExecuteQueryTable.createDDL}${testExecuteQueryTable.insertQuery(Seq("(1,'Andre')","(2,'Joao')","(3,'Maria')"))}")
              resultSet  <- JdbcConnectorService.executeQuery(connection, testExecuteQueryTable.selectAllQuery)
              firstId    <- ZIO.effect({
                resultSet.next
                resultSet.getInt("ID")
              })
              _          <- JdbcConnectorService.closeConnection(connection)
            } yield zio.test.assert(firstId)(equalTo(1))
          test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
        }
      )
    )/*,
    suite("Test Mapped methods from ResultSet")(
      suite("Test JdbcConnectorService.executeQuery")(
        suite("Test string field mappers")(
          testM("Single not null string mapper (success)") {
            case class Cls(v: String)
            val test_case =
              for {
                connection <- JdbcConnectorService.getConnection(ConnectionInfo(testDb.url))
                _          <- JdbcConnectorService.execute(connection, "drop table if exists test_table1; create table test_table1(ID INT PRIMARY KEY,NAME VARCHAR(500));insert into test_table1 values (1,'Andre'),(2,'Joao'),(3,'Maria');")
                resultSet  <- JdbcConnectorService.executeQuery(connection, "select * FROM test_table1;")
                firstId    <- ZIO.effect({
                  resultSet.next
                  resultSet.getInt("ID")
                })
                _          <- JdbcConnectorService.closeConnection(connection)
              } yield zio.test.assert(firstId)(equalTo(1))
            test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
          }
        )
      )
    )*/
  )
}
