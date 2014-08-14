package imadz.actor

import java.util.concurrent.locks.ReentrantLock
import imadz.experiments.cases.MultiTenantsReadSimulator
import akka.actor._
import scala.util.control.NonFatal

/**
 * Created by geek on 14-8-14.
 */
object ActorUtils {


  val lock = new ReentrantLock
  val condition = lock.newCondition()

  def actorOf[A](actorClass: Class[A], actorName: String)(f: ActorRef => Unit) = {
    val system = ActorSystem("Main")
    try {
      val reader = system.actorOf(Props(actorClass), actorName)
      system.actorOf(Props(classOf[Terminator], reader), "app-terminator")
      f(reader)
    } catch {
      case NonFatal(e) ⇒ system.shutdown(); throw e
    }
    lock.lock()
    condition.await()
    lock.unlock()
  }

  class Terminator(app: ActorRef) extends Actor with ActorLogging {
    context watch app

    def receive = {
      case Terminated(_) ⇒
        log.info("application supervisor has terminated, shutting down")
        context.system.shutdown()
        lock.lock()
        condition.signal()
        lock.unlock()
    }
  }


}
