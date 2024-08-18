package fp.game.pvz.model

import scalafx.beans.property.{IntegerProperty, StringProperty, ObjectProperty}
import scalikejdbc._
import fp.game.pvz.util.Database
import scala.util.{ Try, Success, Failure }


class Player(_name: String, _coins: Int, _level: String) {
  val name: StringProperty = new StringProperty(_name)
  val coins: IntegerProperty = IntegerProperty(_coins)
  val level: StringProperty = new StringProperty(_level)

  def save(): Try[Int] = {
    Try(DB autoCommit { implicit session =>
      sql"""
           insert into record (
                               name,
                               coins,
                               level
                               ) values
                                        (
                                         ${name.value},
                                         ${coins.value},
                                         ${level.value}
                                         )
         """.update.apply()
    })
  }

}


object Player extends Database{
  val empty = new Player("", 0, null)

  def initializeTable(): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
           create table player (
               id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
               name varchar(255),
               coins int,
               level varchar(255)
           )

         """.execute.apply()
    }
  }

  def getAllPlayer(count: Int): List[Player] = {
    DB readOnly{implicit session =>
      sql"select * from record order by coins desc fetch first ${count} rows only".map(rs => {
        new Player(
          rs.string("name"),
          rs.int("coins"),
          rs.string("level")
        )
      }).list.apply()
    }
  }

}

