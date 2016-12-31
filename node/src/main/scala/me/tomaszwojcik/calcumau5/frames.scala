package me.tomaszwojcik.calcumau5

import scala.concurrent.Promise

object frames {

  type FrameHandler = (Frame) => Unit

  abstract class Frame

  case class Tell(nodeID: String, msg: AnyRef) extends Frame

  case class Ask(nodeID: String, msg: AnyRef, promise: Promise[AnyRef]) extends Frame

  case object Start extends Frame

  case object Ping extends Frame

  case object Pong extends Frame

}
