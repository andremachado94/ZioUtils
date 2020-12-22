package net.amsoft.zioutils.services.jdbc.unsafe

import java.sql.{ResultSet, Time, Timestamp}

private [jdbc] object ResultSetReader {
  trait Reader[T] {
    def fromResultSet(column: Int, rs: ResultSet): T
  }

  implicit val stringReader: Reader[String] = new Reader[String] {
    override def fromResultSet(column: Int, rs: ResultSet): String = rs getString column
  }
  implicit val stringReaderOpt: Reader[Option[String]] = new Reader[Option[String]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[String] = Option(rs getString column)
  }

  implicit val intReader: Reader[Int] = new Reader[Int] {
    override def fromResultSet(column: Int, rs: ResultSet): Int = rs getInt column
  }
  implicit val intReaderOpt: Reader[Option[Int]] = new Reader[Option[Int]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Int] = Option(rs getInt column)
  }

  implicit val byteReader: Reader[Byte] = new Reader[Byte] {
    override def fromResultSet(column: Int, rs: ResultSet): Byte = rs getByte column
  }
  implicit val byteReaderOpt: Reader[Option[Byte]] = new Reader[Option[Byte]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Byte] = Option(rs getByte column)
  }

  implicit val doubleReader: Reader[Double] = new Reader[Double] {
    override def fromResultSet(column: Int, rs: ResultSet): Double = rs getDouble column

  }
  implicit val doubleReaderOpt: Reader[Option[Double]] = new Reader[Option[Double]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Double] = Option(rs getDouble column)
  }

  implicit val floatReader: Reader[Float] = new Reader[Float] {
    override def fromResultSet(column: Int, rs: ResultSet): Float = rs getFloat column

  }
  implicit val floatReaderOpt: Reader[Option[Float]] = new Reader[Option[Float]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Float] = Option(rs getFloat column)
  }


  implicit val timestampReader: Reader[Timestamp] = new Reader[Timestamp] {
    override def fromResultSet(column: Int, rs: ResultSet): Timestamp = rs getTimestamp column

  }
  implicit val timestampReaderOpt: Reader[Option[Timestamp]] = new Reader[Option[Timestamp]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Timestamp] = Option(rs getTimestamp column)
  }

  implicit val timeReader: Reader[Time] = new Reader[Time] {
    override def fromResultSet(column: Int, rs: ResultSet): Time = rs getTime column
  }
  implicit val timeReaderOpt: Reader[Option[Time]] = new Reader[Option[Time]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Time] = Option(rs getTime column)
  }

  implicit val longReader: Reader[Long] = new Reader[Long] {
    override def fromResultSet(column: Int, rs: ResultSet): Long = rs getLong column
  }
  implicit val longReaderOpt: Reader[Option[Long]] = new Reader[Option[Long]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Long] = Option(rs getLong column)
  }

  implicit val booleanReader: Reader[Boolean] = new Reader[Boolean] {
    override def fromResultSet(column: Int, rs: ResultSet): Boolean = rs getBoolean column
  }
  implicit val booleanReaderOpt: Reader[Option[Boolean]] = new Reader[Option[Boolean]] {
    override def fromResultSet(column: Int, rs: ResultSet): Option[Boolean] = Option(rs getBoolean column)
  }
}
