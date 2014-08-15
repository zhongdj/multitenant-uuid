package imadz.experiments.cases

import imadz.actor.ActorUtils._
import imadz.experiments.pk.Consts._

/**
 * Created by geek on 14-8-14.
 */
object PrepareData extends App {

//  actorOf(classOf[MultiTenantsWriteSimulator], "binary-writer") { writer =>
//    writer ! BinaryPK
//  }
//  actorOf(classOf[MultiTenantsWriteSimulator], "auto-incremental-writer") { writer =>
//    writer ! AutoIncremental
//  }
  actorOf(classOf[MultiTenantsWriteSimulator], "hex-writer") { writer =>
    writer ! HexPK
  }

}
