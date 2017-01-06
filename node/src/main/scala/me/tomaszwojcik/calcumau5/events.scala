package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.types.NodeID

object events {

  object inbound {

    type InEvtHandler = InEvt => Unit

    sealed abstract class InEvt

    case class InMsg(msg: AnyRef, to: NodeID, from: NodeID) extends InEvt

  }

  object outbound {

    type OutEvtHandler = OutEvt => Unit

    sealed abstract class OutEvt

    case class OutMsg(msg: AnyRef, to: NodeID) extends OutEvt

    case class SelfMsg(msg: AnyRef) extends OutEvt

  }

}
