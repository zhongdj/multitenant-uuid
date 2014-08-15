package imadz

import org.jooq._
import org.jooq.impl._
import org.jooq.impl.DSL._
import collection.JavaConversions._
import org.jooq.scala.Conversions._
import imadz.jdbc.Connected
import imadz.experiments.pk.Consts._
import imadz.model.gen.Tables._

/**
 * Created by geek on 14-8-15.
 */
object VisitListenerDemo extends App with Connected {

  val rs = for (r: Record <- e.select(
    TBL_BINARY_PK_UUID.FIRST_NAME,
    TBL_BINARY_PK_UUID.LAST_NAME
  )
    from TBL_BINARY_PK_UUID
    limit 10
    fetch
  ) yield r

}
