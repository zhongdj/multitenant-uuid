package com.example


import fr.janalyse.ssh._
import net.imadz.performance.monitoring._
import akka.actor._
import scala.util.control.NonFatal
import imadz.example.ReadMain
import fr.janalyse.ssh.SSHOptions
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by geek on 14-8-10.
 */
object PerfTests extends App {
  implicit val sshOptions = SSHOptions(host = "dbserver", username = "techop", password = SSHPassword.string2password("hai_5631"))

  val lock = new ReentrantLock
  val condition = lock.newCondition()

  "Query against Binary UUID" collect (List(Cpu, Mem, IO, Network)) run {
    val system = ActorSystem("Main")
    try {
      val reader = system.actorOf(Props(classOf[ReadMain]), "binary-reader")
      system.actorOf(Props(classOf[Terminator], reader), "app-terminator")
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
