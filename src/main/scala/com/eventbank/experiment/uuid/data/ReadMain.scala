package com.eventbank.experiment.uuid.data

import java.util.concurrent.Executor

import akka.actor.Actor
import com.eventbank.experiment.uuid.data.TenantDataGenerator._

import scala.concurrent.ExecutionContext

/**
 * Created by Barry on 8/1/2014.
 */
class ReadMain extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  1 to 100 foreach { x => context.actorOf(TenantRandomRangeReader.props(x), "data-reader-" + x)}

  //context.children foreach { x => x ! ReadOp(HexPK)}
  context.children foreach { x => x ! ReadOp(BinaryPK)}
  //context.children foreach { x => x ! ReadOp(AutoIncremental)}
  var left : Int = 100
  override def receive: Receive = {
    case Complete =>
      println(sender.path + " finished creating data")
      left -= 1
      if (left == 0) context.stop(self)
    case message => println(message)
  }

}
