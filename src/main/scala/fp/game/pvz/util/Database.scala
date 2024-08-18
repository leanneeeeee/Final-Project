package fp.game.pvz.util

import scalikejdbc._
import fp.game.pvz.model.Player

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL: String = "jdbc:derby:pvzDB;create=true;"
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine")

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

object Database extends Database{
  def setupDB() = {
    if (!hasDBInitialize)
      Player.initializeTable()
  }

  def hasDBInitialize : Boolean = {
    DB getTable "Player" match {
      case Some(x) => true
      case None => false
    }
  }
}