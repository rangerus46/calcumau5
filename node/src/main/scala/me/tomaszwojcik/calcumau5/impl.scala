package me.tomaszwojcik.calcumau5

import java.util.concurrent.LinkedBlockingQueue

import me.tomaszwojcik.calcumau5.api.{Context, NodeRef}
import me.tomaszwojcik.calcumau5.events.inbound._
import me.tomaszwojcik.calcumau5.events.outbound._
import me.tomaszwojcik.calcumau5.types.NodeID

import scala.collection.mutable

object impl {

  class ContextImpl extends Context {

    val inEvts = new LinkedBlockingQueue[InEvt]

    val outEvts = new mutable.Queue[OutEvt]

    var outEvtHandler: Option[OutEvtHandler] = None

    // Refs

    lazy val sender = new MutableNodeRef(this)

    lazy val self = new SelfNodeRef(this)

    override def remoteNode(nodeID: NodeID): NodeRef = new RemoteNodeRef(this, nodeID)

    // Operations

    override def die(): Unit = ???

  }

  class BaseNodeRef(ctx: ContextImpl, converter: AnyRef => OutEvt) extends NodeRef {
    override def !(msg: AnyRef): Unit = {
      val evt = converter.apply(msg)
      ctx.outEvtHandler match {
        case Some(handler) => handler.apply(evt)
        case None => ctx.outEvts.enqueue(evt)
      }
    }
  }

  class SelfNodeRef(ctx: ContextImpl)
    extends BaseNodeRef(ctx, msg => SelfMsg(msg))

  class RemoteNodeRef(ctx: ContextImpl, nodeID: NodeID)
    extends BaseNodeRef(ctx, msg => OutMsg(msg, nodeID))

  class MutableNodeRef(ctx: ContextImpl, var nodeID: Option[NodeID] = None)
    extends BaseNodeRef(ctx, msg => OutMsg(msg, nodeID.get))

}
