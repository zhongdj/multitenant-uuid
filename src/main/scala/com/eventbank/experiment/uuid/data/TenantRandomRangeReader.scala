package com.eventbank.experiment.uuid.data

import _root_.scala.annotation.tailrec
import java.util.concurrent.Executor

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import akka.event.LoggingReceive
import com.eventbank.experiment.uuid.data.TenantDataGenerator._
import com.eventbank.model.gen.trans.Tables._

import scala.concurrent.ExecutionContext
import scala.util.Random

import collection.JavaConversions._

import org.jooq._
import org.jooq.impl._
import org.jooq.impl.DSL._
import org.jooq.scala.Conversions._

/**
 * Created by Barry on 8/1/2014.
 */
class TenantRandomRangeReader(tenantId : Int) extends Actor with Connected {
  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]


//  @throws[T](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    context.parent ! Complete
    close
  }

  override def receive: Receive = LoggingReceive {
    case ReadOp(strategy) =>
      strategy match {
        case AutoIncremental =>
          readThroughAuto(0)
        case BinaryPK =>
          readThroughBinary(0)
        case HexPK =>
          readThroughHex(0)
        case Base64PK => throw new UnsupportedOperationException

      }

  }


  val singleMax: Int = 2000

  @tailrec final def readThroughAuto(offset: Int) {
    val num = singleMax//Random.nextInt(singleMax)
    val result = for (r: Record <- e
      select(TBL_AUTO_PK_UUID.FIRST_NAME, TBL_AUTO_PK_UUID.LAST_NAME, TBL_AUTO_PK_UUID.EMAIL, TBL_AUTO_PK_UUID.TENANT_ID)
      from TBL_AUTO_PK_UUID
      where TBL_AUTO_PK_UUID.TENANT_ID.eq(tenantId)
      limit num
      offset offset
      fetch
    ) yield r

    if (result.size > 0) readThroughAuto(offset + result.size)
    else context.stop(self)

  }
  @tailrec final def readThroughBinary(offset: Int) {
    val num = singleMax//Random.nextInt(singleMax)
    val result = for (r: Record <- e
      select(TBL_BINARY_PK_UUID.FIRST_NAME, TBL_BINARY_PK_UUID.LAST_NAME, TBL_BINARY_PK_UUID.EMAIL, TBL_BINARY_PK_UUID.TENANT_ID)
      from TBL_BINARY_PK_UUID
      where TBL_BINARY_PK_UUID.TENANT_ID.eq(tenantId)
      limit num
      offset offset
      fetch
    ) yield r

    if (result.size > 0) readThroughBinary(offset + result.size)
    else context.stop(self)
  }

  @tailrec final def readThroughHex(offset: Int) {
    val num = singleMax//Random.nextInt(singleMax)
    val result = for (r: Record <- e
      select(TBL_16CHAR_PK_UUID.FIRST_NAME, TBL_16CHAR_PK_UUID.LAST_NAME, TBL_16CHAR_PK_UUID.EMAIL, TBL_16CHAR_PK_UUID.TENANT_ID)
      from TBL_16CHAR_PK_UUID
      where TBL_16CHAR_PK_UUID.TENANT_ID === tenantId
      limit num
      offset offset
      fetch
    ) yield r

    if (result.size > 0) readThroughHex(offset + result.size)
    else context.stop(self)
  }
}

object TenantRandomRangeReader {
  def props(tenantId: Int): Props = Props(classOf[TenantRandomRangeReader], tenantId).withDispatcher("my-dispatcher")

}
