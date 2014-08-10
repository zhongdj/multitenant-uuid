package imadz.example

import java.util.concurrent.Executor

import akka.actor.Actor

import scala.concurrent.ExecutionContext
import imadz.example.TenantDataGenerator._

/**
 * Created by Barry on 8/1/2014.
 */
class ReadMain extends Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  9 to 10 foreach { x => context.actorOf(TenantRandomRangeReader.props(x), "data-reader-" + x)}

  var left : Int = 2
  override def receive: Receive = {
    case BinaryPK =>
      context.children foreach { x => x ! ReadOp(BinaryPK)}
    case HexPK =>
      context.children foreach { x => x ! ReadOp(HexPK)}
    case AutoIncremental =>
      context.children foreach { x => x ! ReadOp(AutoIncremental)}
    case Complete =>
      println(sender.path + " finished read data")
      left -= 1
      if (left == 0) context.stop(self)
    case message => println(message)
  }

}
