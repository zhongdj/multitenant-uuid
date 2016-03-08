package imadz.jdbc

import java.sql.{DriverManager, Connection}

import org.jooq._
import org.jooq.impl._
import org.jooq.impl.DSL._
/**
 * Created by Barry on 8/1/2014.
 */

trait Connected {
  Class.forName("com.mysql.jdbc.Driver")
  lazy val connection: Connection = DriverManager.getConnection("jdbc:mysql://dbserver:3306/test", "ebadmin", "28270033")
  connection.setAutoCommit(false)
  val configuration = new DefaultConfiguration()
  configuration.set(connection)
  configuration.set(SQLDialect.MYSQL)
  configuration.set(new DefaultVisitListenerProvider(new DefaultVisitListener() {

    override def visitStart(context: VisitContext): Unit = {
      super.clauseStart(context)
      val c = context.clause
      val p = context.queryPart
      if (c == Clause.SELECT_SELECT && null != context.renderContext) {
         context.renderContext.keyword("SQL_NO_CACHE").sql(" ")
      }
    }
  }))



  configuration.set(new DefaultExecuteListenerProvider(new DefaultExecuteListener() {
    override def executeStart(ctx: ExecuteContext): Unit = {
      super.executeStart(ctx)
      println(ctx.sql())
    }
  }))
  configuration.set(SQLDialect.MYSQL)

  lazy val e = DSL.using(configuration)
  //lazy val e = DSL.using(connection, SQLDialect.MYSQL)


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
