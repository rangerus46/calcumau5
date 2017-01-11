package me.tomaszwojcik.calcumau5.node

import me.tomaszwojcik.calcumau5.api
import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.events.DieEvt
import me.tomaszwojcik.calcumau5.events.inbound.{InEvt, InMsgEvt}
import me.tomaszwojcik.calcumau5.events.outbound.{ErrorEvt, OutEvtHandler}
import me.tomaszwojcik.calcumau5.impl.ContextImpl
import me.tomaszwojcik.calcumau5.types.NodeID
import me.tomaszwojcik.calcumau5.util.Logging

class NodeExec(nodeID: NodeID, c: Class[_ <: api.Node], outEvtHandler: OutEvtHandler)
  extends Runnable
    with Logging {

  private val node: Node = c.newInstance()
  private val nodeCtx = node.ctx.asInstanceOf[ContextImpl]

  private var alive = false

  override def run(): Unit = try {
    log.info(s"Node $nodeID started")

    alive = true
    swapHandler()

    while (alive) {
      val evt = nodeCtx.inEvts.take()
      handleInEvt(evt)
    }

    log.info(s"Node $nodeID stopped")
  } catch {
    // Send an event to the client and rethrow the exception to crash the thread.
    case e: Exception =>
      outEvtHandler.apply(ErrorEvt(e))
      throw e
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
