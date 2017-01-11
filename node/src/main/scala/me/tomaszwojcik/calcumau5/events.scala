package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.types.NodeID

object events {

  object inbound {

    type InEvtHandler = InEvt => Unit

    sealed trait InEvt

    case class InMsgEvt(msg: AnyRef, to: NodeID, from: NodeID) extends InEvt

  }

  object outbound {

    type OutEvtHandler = OutEvt => Unit

    sealed trait OutEvt

    case class OutMsgEvt(msg: AnyRef, to: NodeID) extends OutEvt

    case class SelfMsgEvt(msg: AnyRef) extends OutEvt

    case class LogEvt(s: String) extends OutEvt

    case class ErrorEvt(e: Exception) extends OutEvt

  }

  import inbound._
  import outbound._

  case object DieEvt extends InEvt with OutEvt

}
