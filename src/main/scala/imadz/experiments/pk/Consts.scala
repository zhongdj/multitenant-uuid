package imadz.experiments.pk

/**
 * Created by geek on 14-8-14.
 */
object Consts {
  abstract class UUIDStrategy

  case object AutoIncremental extends UUIDStrategy

  case object BinaryPK extends UUIDStrategy

  case object HexPK extends UUIDStrategy

  case object Base64PK extends UUIDStrategy

  case object ComboPK extends UUIDStrategy

  abstract class Message

  case class InsertOp(strategy: UUIDStrategy) extends Message

  case class ReadOp(strategy: UUIDStrategy) extends Message

  case object Complete extends Message
}
