package com.eventbank.experiment.uuid.data

import _root_.scala.concurrent.ExecutionContext
import _root_.scala.util.Random
import _root_.scala.annotation.tailrec
import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import com.eventbank.model.gen.trans.Tables._
import com.example.UUIDs
import java.sql.DriverManager
import TenantDataGenerator._
import java.sql.Connection
import org.jooq._
import org.jooq.impl._
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import java.util.Hashtable

/**
 * Created by geek on 7/30/14.
 */
class TenantDataGenerator(tenantId: Int) extends Actor with Connected {

  //@scala.throws[T](classOf[scala.Exception])
  override def postStop(): Unit = {
    super.postStop
    sender ! Complete
    close
  }

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  var counter: Int = 0

  def firstName = {
    counter += 1
    "Edwin " + counter
  }

  def lastName = "Veldhuizen " + counter

  def email = "edwin.veldhuizen." + counter + "@eventbank.com"

  private val total: Int = 100000

  val batchMax: Int = 5

  @tailrec final def doGenerateBinary(targetSize: Int = total) {
    if (targetSize > 0) {
      val num = if (targetSize < batchMax) targetSize else Random.nextInt(batchMax)
      if (num > 0) {
        val binaryInsert = e.insertInto(TBL_BINARY_PK_UUID, TBL_BINARY_PK_UUID.ID, TBL_BINARY_PK_UUID.FIRST_NAME, TBL_BINARY_PK_UUID.LAST_NAME, TBL_BINARY_PK_UUID.EMAIL, TBL_BINARY_PK_UUID.TENANT_ID)
        1 to num foreach { x => binaryInsert.values(binaryId, firstName, lastName, email, tenantId)}
        try {
          binaryInsert.execute()
          commit
        }
        finally {
          rollback
        }
      }
      doGenerateBinary(targetSize - num)
    } else {
      close
      context.stop(self)
    }
  }


  def binaryId: Array[Byte] = {
    UUIDs.binaryUUID(tenantId, 1, 1)
  }

  @tailrec final def doGenerateAutoIncr(targetSize: Int) {
    if (targetSize > 0) {
      val num = if (targetSize < batchMax) targetSize else Random.nextInt(batchMax)
      if (num > 0) {
        val autoIncInsert = e.insertInto(TBL_AUTO_PK_UUID, TBL_AUTO_PK_UUID.FIRST_NAME, TBL_AUTO_PK_UUID.LAST_NAME, TBL_AUTO_PK_UUID.EMAIL, TBL_AUTO_PK_UUID.TENANT_ID)
        1 to num foreach { x => autoIncInsert.values(firstName, lastName, email, tenantId)}
        try {
          autoIncInsert.execute()
          commit
        }
        finally {
          rollback
        }
      }

      doGenerateAutoIncr(targetSize - num)
    } else {
      close
      context.stop(self)
    }

  }

  @tailrec final def doGenerateHex(targetSize: Int) {
    if (targetSize > 0) {
      val num = if (targetSize < batchMax) targetSize else Random.nextInt(batchMax)
      if (num > 0) {
        val hexInsert = e.insertInto(TBL_16CHAR_PK_UUID, TBL_16CHAR_PK_UUID.ID, TBL_16CHAR_PK_UUID.FIRST_NAME, TBL_16CHAR_PK_UUID.LAST_NAME, TBL_16CHAR_PK_UUID.EMAIL, TBL_16CHAR_PK_UUID.TENANT_ID)
        1 to num foreach { x => hexInsert.values(hexId, firstName, lastName, email, tenantId)}
        try {
          hexInsert.execute()
          commit
        }
        finally {
          rollback
        }
      }

      doGenerateHex(targetSize - num)
    } else {
      close
      context.stop(self)
    }
  }

  val invokeCounter = new AtomicInteger(0)

  def hexId: String = {
    UUIDs.hexUUID(tenantId, 1, 1)
  }


  override def receive: Receive = LoggingReceive {
    case InsertOp(strategy) => strategy match {
      case AutoIncremental =>
        doGenerateAutoIncr(total)
      case BinaryPK =>
        doGenerateBinary(total)
      case HexPK =>
        doGenerateHex(total)
      case Base64PK =>
    }
  }
}

object TenantDataGenerator {
  def props(tenantId: Int): Props = Props(classOf[TenantDataGenerator], tenantId).withDispatcher("my-dispatcher")


  abstract class UUIDStrategy

  case object AutoIncremental extends UUIDStrategy

  case object BinaryPK extends UUIDStrategy

  case object HexPK extends UUIDStrategy

  case object Base64PK extends UUIDStrategy

  case object ComboPK extends UUIDStrategy

  abstract class Message

  case class InsertOp(strategy: UUIDStrategy) extends Message

  case class ReadOp(strategy: UUIDStrategy) extends Message

  case object Complete extends Message


}
