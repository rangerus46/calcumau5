package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.{Node, NodeContext, NodeRef}
import me.tomaszwojcik.calcumau5.frames.{Ask, Frame, FrameHandler, Tell}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

object impl {

  class NodeContextImpl extends NodeContext {
    var _handler: FrameHandler = new TempFrameHandler

    override def create[A <: Node](nodeID: String)(implicit mf: Manifest[A]): NodeRef = {
      new NodeRefImpl(this, nodeID, serverID = None, Some(mf.runtimeClass), create = true)
    }

    override def getLocal(nodeID: String): NodeRef = {
      new NodeRefImpl(this, nodeID, serverID = None, clazz = None, create = false)
    }

    override def getRemote(nodeID: String, serverID: String): NodeRef = {
      new NodeRefImpl(this, nodeID, Some(serverID), clazz = None, create = false)
    }

    def swapHandler(handler: FrameHandler): Unit = _handler.synchronized {
      _handler match {
        case old: TempFrameHandler => old.transferTo(handler)
        case _ =>
      }
      _handler = handler
    }
  }

  class NodeRefImpl(
    val ctx: NodeContextImpl,
    val nodeID: String,
    val serverID: Option[String],
    val clazz: Option[Class[_]],
    val create: Boolean
  ) extends NodeRef {
    override def tell(msg: AnyRef): Unit = ctx._handler.synchronized {
      ctx._handler(Tell(nodeID, msg))
    }

    override def ask(msg: AnyRef): Future[AnyRef] = {
      val promise = Promise[AnyRef]
      ctx._handler(Ask(nodeID, msg, promise))
      promise.future
    }
  }

  class TempFrameHandler extends FrameHandler {
    private val frames = new mutable.Queue[Frame]

    override def apply(frame: Frame): Unit = frames.synchronized {
      frames += frame
    }

    def transferTo(handler: FrameHandler): Unit = frames.synchronized {
      while (frames.nonEmpty) {
        val frame = frames.dequeue()
        handler.apply(frame)
      }
    }
  }

}
