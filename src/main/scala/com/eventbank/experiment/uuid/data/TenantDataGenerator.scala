package com.eventbank.experiment.uuid.data

import _root_.scala.concurrent.ExecutionContext
import _root_.scala.util.Random
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
class TenantDataGenerator(tenantId: Int) extends Actor {

  //@scala.throws[T](classOf[scala.Exception])
  override def postStop(): Unit = {

    println("actor stopped =========================================================================================")
    if (!connection.isClosed) connection.close()
  }

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  Class.forName("com.mysql.jdbc.Driver")
  val connection: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "ebdev", "000000")
  connection.setAutoCommit(false)
  val e = DSL.using(connection, SQLDialect.MYSQL)

  var counter: Int = 0

  def firstName = {
    counter += 1
    "Edwin " + counter
  }

  def lastName = "Veldhuizen " + counter

  def email = "edwin.veldhuizen." + counter + "@eventbank.com"


  private val total: Int = 100000

  def doGenerateBinary(targetSize: Int = total) {
    if (targetSize > 0) {
      val num = Random.nextInt(2000)
      if (num > 0) {
        val binaryInsert = e.insertInto(TBL_BINARY_PK_UUID, TBL_BINARY_PK_UUID.ID, TBL_BINARY_PK_UUID.FIRST_NAME, TBL_BINARY_PK_UUID.LAST_NAME, TBL_BINARY_PK_UUID.EMAIL, TBL_BINARY_PK_UUID.TENANT_ID)
        1 to num foreach { x => binaryInsert.values(binaryId, firstName, lastName, email, tenantId)}
        try {
          binaryInsert.execute()
          connection.commit()
        }
        finally {
          connection.rollback()
        }
      }

      doGenerateBinary(targetSize - num)
    } else {
      connection.close()
      context.stop(self)
    }
  }

  def binaryId: Array[Byte] = {
    UUIDs.binaryUUID(tenantId, 1, 1)
  }

  def doGenerateHex(targetSize: Int) {
    if (targetSize > 0) {
      val num = Random.nextInt(2000)
      if (num > 0) {
        val hexInsert = e.insertInto(TBL_16CHAR_PK_UUID, TBL_16CHAR_PK_UUID.ID, TBL_16CHAR_PK_UUID.FIRST_NAME, TBL_16CHAR_PK_UUID.LAST_NAME, TBL_16CHAR_PK_UUID.EMAIL, TBL_16CHAR_PK_UUID.TENANT_ID)
        1 to num foreach { x => hexInsert.values(hexId, firstName, lastName, email, tenantId)}
        try {
          hexInsert.execute()
          connection.commit()
        }
        finally {
          connection.rollback()
        }
      }

      doGenerateHex(targetSize - num)
    } else {
      connection.close()
      context.stop(self)
    }
  }

  val invokeCounter = new AtomicInteger(0)
  val idset = new Hashtable[String, Int]()

  def hexId: String = {
    val invokeCountLocal: Int = invokeCounter.incrementAndGet
    println("hexId Number: " + invokeCountLocal)
    val id = UUIDs.hexUUID(tenantId, 1, 1)

    if (idset.containsKey(id)) {
      throw new IllegalStateException()
    } else {
      idset.put(id, invokeCountLocal)
    }
    id
  }

  override def receive: Receive = LoggingReceive {


    case InsertOp(strategy) => strategy match {
      case AutoIncremental =>
      case BinaryPK =>
        doGenerateBinary(total)
        sender ! Complete
      case HexPK =>
        doGenerateHex(total)
        sender ! Complete
      case Base64PK =>
    }


  }
}

object TenantDataGenerator {
  def props(tenantId: Int): Props = Props(classOf[TenantDataGenerator], tenantId)


  abstract class UUIDStrategy

  case object AutoIncremental extends UUIDStrategy

  case object BinaryPK extends UUIDStrategy

  case object HexPK extends UUIDStrategy

  case object Base64PK extends UUIDStrategy

  abstract class Message

  case class InsertOp(strategy: UUIDStrategy) extends Message

  case object Complete extends Message


}
