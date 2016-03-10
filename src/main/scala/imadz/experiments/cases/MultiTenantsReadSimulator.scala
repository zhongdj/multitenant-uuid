package imadz.experiments.cases

import java.util.concurrent.Executor

import akka.actor.Actor

import scala.concurrent.ExecutionContext
import imadz.experiments.pk.{TenantRandomRangeReader, TenantDataGenerator}
import imadz.experiments.pk.Consts._

/**
 * Created by Barry on 8/1/2014.
 */
class MultiTenantsReadSimulator extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  1 to 10 foreach { x => context.actorOf(TenantRandomRangeReader.props(x), "data-reader-" + x)}

  var left : Int = 10
  override def receive: Receive = {
    case BinaryPK =>
      context.children foreach { x => x ! ReadOp(BinaryPK)}
    case HexPK =>
      context.children foreach { x => x ! ReadOp(HexPK)}
    case AutoIncremental =>
      context.children foreach { x => x ! ReadOp(AutoIncremental)}
    case ComboPK =>
      context.children foreach { x => x ! ReadOp(ComboPK)}
    case Complete =>
      println(sender.path + " finished read data")
      left -= 1
      if (left == 0) context.stop(self)
    case message => println(message)
  }

}
