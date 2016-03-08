package imadz.experiments.pk

import _root_.scala.concurrent.ExecutionContext
import _root_.scala.util.Random
import _root_.scala.annotation.tailrec
import akka.actor.{Props, Actor}
import akka.event.LoggingReceive
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import imadz.uuid.UUIDs
import imadz.jdbc.Connected
import imadz.model.gen.Tables._
import imadz.experiments.pk.Consts._

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
  var base = 0
  @tailrec final def doGenerateCombo(targetSize: Int) {
    if (targetSize > 0) {
      val num = if (targetSize < batchMax) targetSize else Random.nextInt(batchMax)
      if (num > 0) {
        val comboInsert = e.insertInto(TBL_COMBO_PK, TBL_COMBO_PK.TENANT_ID, TBL_COMBO_PK.ID, TBL_COMBO_PK.FIRST_NAME, TBL_COMBO_PK.LAST_NAME, TBL_COMBO_PK.EMAIL)
        1 to num foreach { x => comboInsert.values(tenantId, base + x, firstName, lastName, email)}
        try {
          base += comboInsert.execute()
          commit
        }
        finally {
          rollback
        }
      }

      doGenerateCombo(targetSize - num)
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
      case ComboPK =>
        doGenerateCombo(total)
      case Base64PK =>
    }
  }
}

object TenantDataGenerator {
  def props(tenantId: Int): Props = Props(classOf[TenantDataGenerator], tenantId).withDispatcher("my-dispatcher")





}
