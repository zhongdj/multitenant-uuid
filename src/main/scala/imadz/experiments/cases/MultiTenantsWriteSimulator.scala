package imadz.experiments.cases

import akka.actor.Actor
import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import imadz.experiments.pk.TenantDataGenerator
import imadz.experiments.pk.Consts._
/**
 * Created by geek on 7/30/14.
 */
class MultiTenantsWriteSimulator extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  1 to 100 foreach { x => context.actorOf(TenantDataGenerator.props(x), "data-gen-" + x)}

  var left : Int = 100
  override def receive: Receive = {
    case Complete =>
      println(sender.path + " finished creating data")
      left -= 1
      if (left == 0) context.stop(self)
    case BinaryPK =>
      context.children foreach { x => x ! InsertOp(BinaryPK)}
    case AutoIncremental =>
      context.children foreach { x => x ! InsertOp(AutoIncremental)}
    case HexPK =>
      context.children foreach { x => x ! InsertOp(HexPK)}
    case ComboPK =>
      context.children foreach { _ ! InsertOp(ComboPK)}
    case message => println(message)
  }
}
