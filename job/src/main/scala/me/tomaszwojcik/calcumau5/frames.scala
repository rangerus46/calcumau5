package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.NodeRef

import scala.concurrent.Promise

object frames {

  abstract class Frame

  case class TellFrame(ref: NodeRef, msg: AnyRef) extends Frame

  case class AskFrame(ref: NodeRef, msg: AnyRef, promise: Promise[AnyRef]) extends Frame

  case class NewNodeFrame(ref: NodeRef) extends Frame

  case class GetNodeFrame(ref: NodeRef) extends Frame

}
