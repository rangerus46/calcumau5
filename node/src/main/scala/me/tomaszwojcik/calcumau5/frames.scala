package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.NodeRef

import scala.concurrent.Promise

object frames {

  type FrameHandler = (Frame) => Unit

  abstract class Frame

  case class TellFrame(ref: NodeRef, msg: AnyRef) extends Frame

  case class AskFrame(ref: NodeRef, msg: AnyRef, promise: Promise[AnyRef]) extends Frame

  case object Start extends Frame

  case object Ping extends Frame

  case object Pong extends Frame

}
