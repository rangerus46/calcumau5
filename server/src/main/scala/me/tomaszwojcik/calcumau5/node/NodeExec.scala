package me.tomaszwojcik.calcumau5.node

import me.tomaszwojcik.calcumau5.api
import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.events.DieEvt
import me.tomaszwojcik.calcumau5.events.inbound.{InEvt, InMsgEvt}
import me.tomaszwojcik.calcumau5.events.outbound.OutEvtHandler
import me.tomaszwojcik.calcumau5.impl.ContextImpl
import me.tomaszwojcik.calcumau5.types.NodeID
import me.tomaszwojcik.calcumau5.util.Logging

class NodeExec(nodeID: NodeID, c: Class[_ <: api.Node], outEvtHandler: OutEvtHandler)
  extends Runnable
    with Logging {

  private val node: Node = c.newInstance()
  private val nodeCtx = node.ctx.asInstanceOf[ContextImpl]

  private var alive = false

  override def run(): Unit = {
    log.info(s"Node $nodeID started")

    alive = true
    swapHandler()

    while (alive) {
      val evt = nodeCtx.inEvts.take()
      handleInEvt(evt)
    }

    log.info(s"Node $nodeID stopped")
  }

  def pushEvent(evt: InEvt): Unit = nodeCtx.inEvts.put(evt)

  private def swapHandler(): Unit = {
    nodeCtx.outEvtHandler = Some(outEvtHandler)
    while (nodeCtx.outEvts.nonEmpty) {
      val evt = nodeCtx.outEvts.dequeue()
      outEvtHandler.apply(evt)
    }
  }

  private def handleInEvt(evt: InEvt): Unit = evt match {
    case InMsgEvt(msg, _, from) =>
      nodeCtx.sender.nodeID = Some(from)
      node.receive(msg)
      nodeCtx.sender.nodeID = None

    case DieEvt =>
      alive = false
  }

}
