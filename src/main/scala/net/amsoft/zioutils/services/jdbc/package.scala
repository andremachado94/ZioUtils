package net.amsoft.zioutils.services

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement, Time, Timestamp}

import com.sun.tools.javac.code.TypeTag
import net.amsoft.zioutils.services.jdbc.JdbcConnectorService.ConnectionInfo
import zio.Has
import zio.blocking.Blocking

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

package object jdbc {
  type JdbcConnector = Has[JdbcConnectorService.Service] with Has[Blocking.Service]

  /**
   * Private object only available in jdbc package scope. Contains all the unsafe methods that will be wrapped in
   * effects
   */
  private[jdbc] object side_effects {

    def getConnection(connectionInfo: ConnectionInfo): Either[Exception, Connection] = {
      wrap[ConnectionInfo, Connection](connectionInfo, info => {
        info.driverClassName match {
          case Some(c) => Class.forName(c)
          case None => Unit
        }
        DriverManager.getConnection(info.url, info.username, info.password)
      })
    }

    def closeConnection(connection: Connection): Either[Exception, Unit] = {
      wrap[Connection, Unit](connection, c => {
        if (!c.isClosed) c.close()
      })
    }

    def execute(statement: Statement, sqlQuery: String): Either[Exception, Boolean] = {
      wrap[Statement, String, Boolean](statement, sqlQuery, (s, q) => s.execute(q))
    }

    def createStatement(connection: Connection): Either[Exception, Statement] = {
      wrap[Connection, Statement](connection, _.createStatement())
    }

    def executeQuery(statement: Statement, sqlQuery: String): Either[Exception, ResultSet] = {
      wrap[Statement, String, ResultSet](statement, sqlQuery, (s, q) => s.executeQuery(q))
    }

    def executeUpdate(statement: Statement, sqlQuery: String): Either[Exception, Int] = {
      wrap[Statement, String, Int](statement, sqlQuery, (s, q) => s.executeUpdate(q))
    }

    def executeLargeUpdate(statement: Statement, sqlQuery: String): Either[Exception, Long] = {
      wrap[Statement, String, Long](statement, sqlQuery, (s, q) => s.executeLargeUpdate(q))
    }

    /** Executes f(A1) => B in a Try and maps the result to an Either[Exception, B]
     *
     * @param a1 first argument of f
     * @param f method f
     * @return f(A1) => B => Left(Exception) if the execution fails
     *         f(A1) => B => Right(B) if the execution succeeds
     */
    private def wrap[A1, B](a1: A1, f: A1 => B): Either[Exception, B] = {
      Try(f(a1)) match {
        case Success(result) => Right(result)
        case Failure(e: Exception) => Left(e)
      }
    }

    /** Executes f(A1,A2) => B in a Try and maps the result to an Either[Exception, B]
     *
     * @param a1 first argument of f
     * @param a2 second argument of f
     * @param f method f
     * @return f(A1,A2) => B => Left(Exception) if the execution fails
     *         f(A1,A2) => B => Right(B) if the execution succeeds
     */
    private def wrap[A1, A2, B](a1: A1, a2: A2, f: (A1, A2) => B): Either[Exception, B] = {
      Try(f(a1, a2)) match {
        case Success(result) => Right(result)
        case Failure(e: Exception) => Left(e)
      }
    }
  }
}