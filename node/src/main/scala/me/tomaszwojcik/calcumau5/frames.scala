package me.tomaszwojcik.calcumau5

object frames {

  type FrameHandler = Frame => Unit

  sealed abstract class Frame

  case class Message(fromID: String, toID: String, payload: Array[Byte]) extends Frame

  case class Run(nodes: Map[String, String]) extends Frame

  case object Ping extends Frame

  case object Pong extends Frame

  case object Disconnect extends Frame

  case class File(bytes: Array[Byte]) extends Frame

}
