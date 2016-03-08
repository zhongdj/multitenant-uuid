package imadz.experiments.cases

import fr.janalyse.ssh.{SSHPassword, SSHOptions}
import imadz.actor.ActorUtils._
import imadz.experiments.pk.Consts._
import net.imadz.performance.PerformanceDataUI
import net.imadz.performance.monitoring._

/**
 * Created by geek on 14-8-14.
 */
object PrepareData extends App {

  implicit val sshOptions = SSHOptions(host = "dbserver", username = "root", password = SSHPassword.string2password("28270033"))

  val xs = "Binary UUID Write" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsWriteSimulator], "binary-writer") { writer =>
      writer ! BinaryPK
    }
  }

  val ys = "AutoIncremental PK" collect (List(Cpu, Mem, IO, Network)) run {
      actorOf(classOf[MultiTenantsWriteSimulator], "auto-incremental-writer") { writer =>
        writer ! AutoIncremental
      }
  }

  val zs = "Hex PK" collect (List(Cpu, Mem, IO, Network)) run {
      actorOf(classOf[MultiTenantsWriteSimulator], "hex-writer") { writer =>
        writer ! HexPK
      }
  }

  val cs = "Combo PK" collect (List(Cpu, Mem, IO, Network)) run {
    actorOf(classOf[MultiTenantsWriteSimulator], "combo-writer") { writer =>
      writer ! ComboPK
    }
  }

  /** For Desktop ONLY
  val iox = cs ::: xs ::: ys ::: zs filter (isIO)


  def isIO : (Monitor) => Boolean = {
    case x: IOMonitor => true
    case _ => false
  }

  PerformanceDataUI.sources = iox map {
    _.logFile.get + ".updated"
  }

  PerformanceDataUI.main(Array[String]())
  */
}
