package imadz.uuid

import com.fasterxml.uuid.{EthernetAddress, Generators}
import scala.collection.mutable

object UUIDs {

  lazy val generator = {
    val addr = EthernetAddress.fromInterface()
    if (null != addr)
      Generators.timeBasedGenerator(addr)
    else
      Generators.timeBasedGenerator()
  }

  def binaryUUID(tenant: Int, objectType: Int, times: Int = 1) = {
    val innerUUID = generator.generate()
    val result = new Array[Byte](22)

    tenantBytes(tenant, result, 0)
    result.update(3, (objectType >>> 12 & 0xFF).toByte)
    result.update(4, (objectType >>> 4 & 0xFF).toByte)
    result.update(5, (objectType << 4 & 0xF0 | times & 0x0F).toByte)
    long2Bytes(innerUUID.getMostSignificantBits, result, 6)
    long2Bytes(innerUUID.getLeastSignificantBits, result, 14)

    result
  }

  def hexUUID(tenant: Int, objectType: Int, times: Int = 1) = {
    binaryUUID(1, 1, 1).toList.map { x => byte2Hex(x)}.mkString
    //generator.generate().toString
    //UUID.randomUUID().toString
  }

  val number = '0' to '9' toList
  val lowercase = 'a' to 'z' toList
  val uppercase = 'A' to 'Z' toList
  val alphabet: Array[Char] = number ::: lowercase ::: uppercase ::: ('+' :: '/' :: Nil) toArray

  val binaryLong: Long = 0x00000000000000000000FFL

  def base64UUID(tenant: Int, objectType: Int, times: Int = 1) = {
    val bytes = binaryUUID(1, 1, 1)

    val tenantChars = to64(byte2Long(bytes(0)) << 16 | byte2Long(bytes(1)) << 8 | byte2Long(bytes(2)), 4)
    val objectTypeChars = to64(byte2Long(bytes(3)) << 12 | byte2Long(bytes(4)) << 4 | byte2Long( bytes(5) & 0xF0 toByte ) >>> 4, 4)
    val timesChar = to64(byte2Long(bytes(5)) & 0x0F, 1)
    val high = to64(byte2Long(bytes(6)) << 56 | byte2Long(bytes(7)) << 48 | byte2Long(bytes(8)) << 40 | byte2Long(bytes(9)) << 32 | byte2Long(bytes(10)) << 24 | byte2Long(bytes(11)) << 16 | byte2Long(bytes(12)) << 8 | byte2Long(bytes(13)), 11)
    val low = to64(byte2Long(bytes(14)) << 56 | byte2Long(bytes(15)) << 48 | byte2Long(bytes(16)) << 40 | byte2Long(bytes(17)) << 32 | byte2Long(bytes(18)) << 24 | byte2Long(bytes(19)) << 16 | byte2Long(bytes(20)) << 8 | byte2Long(bytes(21)), 11)
    val builder = new StringBuilder
    builder.append(tenantChars).append(objectTypeChars).append(timesChar).append(high).append(low) toString
  }

  def byte2Long(b: Byte) = binaryLong & b

  /**
    * Extract three bytes of tenentId and put them to result.
   *  For example:
   *  tenantId in binary: 00000000 00100001 00001000 00000011
   *           result[0]: teneantId >>>16 => 00000000 00000000 00000000 00100001 & 0xFF =>  00100001
   *           result[1]: teneantId >>>8  => 00000000 00000000 00100001 00001000 & 0xFF =>  00001000
   *           result[2]: teneantId >>>>0 => 00000000 00100001 00001000 00000011 & 0xFF =>  00000011
   *
    * @param tenantId
    * @param result
    * @param offset
    */
  def tenantBytes(tenantId: Int, result: Array[Byte], offset: Int) = {
    3 to 1 by -1 foreach { x =>
      result.update(offset + 3 - x, tenantId >>> 8 * (x - 1) & 0xFF toByte)
    }
  }

  def long2Bytes(value: Long, result: Array[Byte], offset: Int) = {
    8 to 1 by -1 foreach { x =>
      result.update(offset + 8 - x, value >>> 8 * (x - 1) & 0xFF toByte)
    }
    result
  }


  def byte2Hex(byte: Byte): String = {
    val hi = 1L << 8
    (hi | (hi - 1) & byte).toHexString.substring(1)
  }

  def to64(l: Long, length: Int): String = {
    if (length <= 0) ""
    else if (l <= 0) to64(l >>> 6, length - 1) + "0"
    else
      to64(l >>> 6, length - 1) + alphabet((l & 63).toInt)
  }


  def main(args: Array[String]): Unit = {
    val key = binaryUUID(1, 1, 1).toList.map { x => byte2Hex(x)}.mkString
    println(key)
    val key2 = hexUUID(1, 1, 1)
    println(key2)
    val key3 = base64UUID(1, 1, 1)
    println(key3)
    println(key3.length)

    val ids = new mutable.HashSet[String]

    1 to Integer.MAX_VALUE foreach { _ =>
      val id = hexUUID(1,1,1)
      if (!ids.add(id)) throw new RuntimeException
    }
  }
}
