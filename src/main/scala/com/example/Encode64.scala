package com.example

/**
 * Created by geek on 7/27/14.
 */
object Encode64 extends App {

  val number = ('0' to '9') toList
  val lowerChars = ('a' to 'z') toList
  val upperChars = ('A' to 'Z') toList

  val alphabet = (number ::: lowerChars ::: upperChars ::: ('+' :: '/' :: Nil)) toArray

  println(alphabet mkString " ")

  //val string: String = UUID.randomUUID().toString
  val string = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"
  val id = {
    string.split("-") map (x => java.lang.Long.parseLong(x.toUpperCase, 16)) map {
      to64
    }
  }

  val x: String = id mkString

  println(x)

  println(x.length)

  def to64(l: Long): String = {
    if (l <= 0) ""
    else
      to64(l >> 6) + alphabet((l & 63).toInt)
  }

  def to64(l: Long, length: Int): String = {
    if (length <= 0) ""
    else if (l <= 0) to64(l >>> 6, length - 1) + "0"
    else
      to64(l >>> 6, length - 1) + alphabet((l & 63).toInt)
  }

  println(to64(System.currentTimeMillis()))

  println(to64(1L, 3))
}
