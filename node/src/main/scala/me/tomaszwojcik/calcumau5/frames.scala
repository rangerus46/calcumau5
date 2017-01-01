package me.tomaszwojcik.calcumau5

object frames {

  type FrameHandler = Frame => Unit

  sealed abstract class Frame

  case class Message(fromID: String, toID: String, payload: AnyRef) extends Frame

  case object Start extends Frame

  case object Ping extends Frame

  case object Pong extends Frame

}
