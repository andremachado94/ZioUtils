import jdbc.JdbcConnectorServiceTest.jdbcSuite
import jdbc.ResultSetMapperTest.rsMapperSuite
import zio.test.{DefaultRunnableSpec, ZSpec, suite}

object MainTest extends DefaultRunnableSpec with org.scalatest.Suite {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = mainSuite

  val mainSuite = suite("All tests")(
    jdbcSuite,
    rsMapperSuite
  )
}
