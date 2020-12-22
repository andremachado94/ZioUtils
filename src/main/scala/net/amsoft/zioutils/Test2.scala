package net.amsoft.zioutils
import net.amsoft.zioutils.services.jdbc.{JdbcConnector, JdbcConnectorService}
import zio.blocking.Blocking
import zio.console.Console
import zio.console
import zio.{ExitCode, URIO, ZIO}

case class Test2(aaaa:Int, bbb:String)
/*
object AAA extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program(args).provideLayer(Console.live ++ JdbcConnectorService.live ++ Blocking.live).exitCode

  private def program(args: List[String]): ZIO[JdbcConnector with Console, Exception, Unit] = {
    for {
      res <- test_logic("jdbc:h2:...")
      _   <- console.putStrLn(res.map(_.aa).mkString("\n"))
    } yield ()
  }

  case class TestClass(aa: String)

  def test_logic(url: String, username: String = "", password: String = ""): ZIO[JdbcConnector, Exception, Seq[TestClass]] = {
    for {
      conn <- JdbcConnectorService.getConnection(JdbcConnectorService.ConnectionInfo(url, username, password))
      res  <- JdbcConnectorService.executeQuery(conn, "SELECT AA FROM XX", TestClass.apply)
      _    <- JdbcConnectorService.closeConnection(conn)
    } yield res
  }


} */


object CodeWriter extends App {
  val s = 1 to 22
  val s2 = s.map(x => 1 to x)
  s2.foreach(println)

  s2.foreach(r => {
    println(s"def executeQuery[${r.map(x => f"T$x").mkString(",")},R](statement: Statement, sqlQuery: String, constructor: (${r.map(x => f"T$x").mkString(",")}) => R)(implicit ${r.map(x => f"evT$x: Reader[T$x]").mkString(",")}): ZIO[JdbcConnector, Exception, Seq[R]] = executeQuery(statement, sqlQuery).flatMap(x => ResultSetMapper.zParse(ResultSetMapper.apply(constructor.apply _), x))")
    println(s"def executeQuery[${r.map(x => f"T$x").mkString(",")},R](connection: Connection, sqlQuery: String, constructor: (${r.map(x => f"T$x").mkString(",")}) => R)(implicit ${r.map(x => f"evT$x: Reader[T$x]").mkString(",")}): ZIO[JdbcConnector, Exception, Seq[R]] = createStatement(connection).flatMap(statement => executeQuery(statement, sqlQuery, constructor.apply _))")
  })
}