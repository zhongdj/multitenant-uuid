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

  implicit val sshOptions = SSHOptions(host = "dbserver", username = "root", password = SSHPassword.string2password("28270033"))

  val xs = "Binary UUID" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "binary-reader") { reader =>
      reader ! BinaryPK
    }
  }

  val ys = "AutoIncremental PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "auto-incremental-reader") { reader =>
      reader ! AutoIncremental
    }
  }

  val zs = "Hex PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "hex-reader") { reader =>
      reader ! HexPK
    }
  }

  val cs = "Combo PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsReadSimulator], "combo-reader") { reader =>
      reader ! ComboPK
    }
  }

  val iox = cs ::: xs ::: ys ::: zs filter (isIO)

  def isIO : (Monitor) => Boolean = {
    case x: IOMonitor => true
    case _ => false
  }

  PerformanceDataUI.sources = iox map {
    _.logFile.get + ".updated"
  }

  PerformanceDataUI.main(Array[String]())

}
