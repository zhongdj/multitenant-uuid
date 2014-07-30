package com.eventbank.experiment.uuid.data

import akka.actor.Actor
import com.eventbank.experiment.uuid.data.TenantDataGenerator.{HexPK, Complete, BinaryPK, InsertOp}
import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext

/**
 * Created by geek on 7/30/14.
 */
class Main extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  1 to 100 foreach { x => context.actorOf(TenantDataGenerator.props(x), "data-gen-" + x)}

//  context.children foreach { x => x ! InsertOp(HexPK)}
  context.children foreach { x => x ! InsertOp(BinaryPK)}

  override def receive: Receive = {
    case Complete => println(sender.path + " finished creating data")
    case message => println(message)
  }
}
