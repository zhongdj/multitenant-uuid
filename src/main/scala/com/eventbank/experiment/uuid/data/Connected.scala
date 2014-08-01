package com.eventbank.experiment.uuid.data

import java.sql.{DriverManager, Connection}

import org.jooq.{SelectQuery, VisitContext, Configuration, SQLDialect}
import org.jooq.impl.{DefaultVisitListener, DefaultVisitListenerProvider, DefaultConfiguration, DSL}

/**
 * Created by Barry on 8/1/2014.
 */
trait Connected {
  Class.forName("com.mysql.jdbc.Driver")
  lazy val connection: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "ebdev", "000000")
  connection.setAutoCommit(false)
  val configuration = new DefaultConfiguration()
  configuration.set(connection)
  configuration.set(SQLDialect.MYSQL)
  configuration.set(new DefaultVisitListenerProvider(new DefaultVisitListener() {
    override def clauseStart(context: VisitContext): Unit = {
      super.clauseStart(context)
      val oldPart = context.queryPart()
      println(oldPart.getClass + ":" + oldPart.toString)
    }
  }))

  lazy val e = DSL.using(connection, SQLDialect.MYSQL)


  def commit {
    connection.commit()
  }

  def close {
    if (!connection.isClosed) connection.close()
  }

  def rollback {
    connection.rollback()
  }

}
