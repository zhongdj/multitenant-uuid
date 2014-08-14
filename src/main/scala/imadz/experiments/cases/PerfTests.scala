package imadz.experiments.cases

import fr.janalyse.ssh._
import net.imadz.performance.monitoring._
import akka.actor._
import fr.janalyse.ssh.SSHOptions
import net.imadz.performance.PerformanceDataUI
import imadz.actor.ActorUtils._
import imadz.experiments.pk.Consts._

/**
 * Created by geek on 14-8-10.
 */
object PerfTests extends App {

  implicit val sshOptions = SSHOptions(host = "dbserver", username = "root", password = SSHPassword.string2password("111111"))

  val xs = "Query against Binary UUID" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "binary-reader") { reader =>
      reader ! BinaryPK
    }
  }

  val ys = "Query against AutoIncremental PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "auto-incremental-reader") { reader =>
      reader ! AutoIncremental
    }
  }

  val zs = "Query against AutoIncremental PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "hex-reader") { reader =>
      reader ! HexPK
    }
  }

  val iox = xs ::: ys ::: zs filter (isIO)

  def isIO : (Monitor) => Boolean = {
    case x: IOMonitor => true
    case _ => false
  }

  PerformanceDataUI.sources = iox map {
    _.logFile.get
  }

  PerformanceDataUI.main(Array[String]())

}
