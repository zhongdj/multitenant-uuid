package com.eventbank.experiment.uuid.data

import akka.actor.Actor
import com.eventbank.experiment.uuid.data.TenantDataGenerator._
import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import com.eventbank.experiment.uuid.data.TenantDataGenerator.InsertOp

/**
 * Created by geek on 7/30/14.
 */
class WriterMain extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  1 to 100 foreach { x => context.actorOf(TenantDataGenerator.props(x), "data-gen-" + x)}

  //context.children foreach { x => x ! InsertOp(HexPK)}
  //context.children foreach { x => x ! InsertOp(BinaryPK)}
  context.children foreach { x => x ! InsertOp(AutoIncremental)}
  var left : Int = 100
  override def receive: Receive = {
    case Complete =>
      println(sender.path + " finished creating data")
      left -= 1
      if (left == 0) context.stop(self)
    case message => println(message)
  }
}
