package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.events.EventHandler

class ReadyContextState(nodeID: String, eventHandler: EventHandler) extends ContextState {
  override def sendToRemote(msg: AnyRef, to: String): Unit = {
    val evt = events.OutboundMessage(payload = msg, to, from = nodeID)
    eventHandler.apply(evt)
  }

  override def sendToSelf(msg: AnyRef): Unit = {
    val evt = events.InboundMessage(payload = msg, to = nodeID, from = nodeID)
    eventHandler.apply(evt)
  }
}
