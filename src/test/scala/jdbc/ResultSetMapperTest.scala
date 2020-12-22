package jdbc

import net.amsoft.zioutils.services.jdbc.JdbcConnectorService
import net.amsoft.zioutils.services.jdbc.JdbcConnectorService.ConnectionInfo
import zio.ZIO
import zio.blocking.Blocking
import zio.test.Assertion.{anything, equalTo, fails, isSubtype}
import zio.test._

object ResultSetMapperTest extends {
  val rsMapperSuite: Spec[Any, TestFailure[Throwable], TestSuccess] = suite("ResultSet Mapper") (
    suite("Test Mapped methods from ResultSet")(
      suite("Test JdbcConnectorService.executeQuery")(
        suite("Test string field mappers")(
          testM("Single not null string mapper (success)") {
            case class Cls(v: String)
            val test_case =
              for {
                connection <- JdbcConnectorService.getConnection(ConnectionInfo(testSingleNotNullStringColumnTable.h2DbInfo.url))
                _          <- JdbcConnectorService.execute(connection, f"${testSingleNotNullStringColumnTable.dropDDL}${testSingleNotNullStringColumnTable.createDDL}${testSingleNotNullStringColumnTable.insertQuery(Seq("('Test1')", "('Test2')"))}")
                result     <- JdbcConnectorService.executeQuery(connection, testSingleNotNullStringColumnTable.selectAllQuery, Cls.apply _)
                numOfRows  <- ZIO.effect(result.size)
                _          <- JdbcConnectorService.closeConnection(connection)
              } yield zio.test.assert(numOfRows)(equalTo(2)) && zio.test.assert(result)(equalTo(Seq(Cls("Test1"), Cls("Test2"))))
            test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
          },
          testM("Single nullable string mapper (success)") {
            case class Cls(v: Option[String])
            val test_case =
              for {
                connection <- JdbcConnectorService.getConnection(ConnectionInfo(testSingleNullableStringColumnTable.h2DbInfo.url))
                _          <- JdbcConnectorService.execute(connection, f"${testSingleNullableStringColumnTable.dropDDL}${testSingleNullableStringColumnTable.createDDL}${testSingleNullableStringColumnTable.insertQuery(Seq("('Test1')", "(null)"))}")
                result     <- JdbcConnectorService.executeQuery(connection, testSingleNullableStringColumnTable.selectAllQuery, Cls.apply _)
                numOfRows  <- ZIO.effect(result.size)
                _          <- JdbcConnectorService.closeConnection(connection)
              } yield zio.test.assert(numOfRows)(equalTo(2)) && zio.test.assert(result)(equalTo(Seq(Cls(Some("Test1")), Cls(None))))
            test_case.provideLayer(JdbcConnectorService.live ++ Blocking.live)
          }
        )
      )
    )
  )
}
