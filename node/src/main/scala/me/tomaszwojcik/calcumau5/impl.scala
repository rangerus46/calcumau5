package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.{NodeContext, NodeRef}
import me.tomaszwojcik.calcumau5.frames._

import scala.collection.mutable
import scala.concurrent.Future

object impl {

  class NodeContextImpl extends NodeContext {
    var _handler: FrameHandler = new TempFrameHandler

    def swapHandler(handler: FrameHandler): Unit = _handler.synchronized {
      _handler match {
        case old: TempFrameHandler => old.transferTo(handler)
        case _ =>
      }
      _handler = handler
    }

    override def remoteNode(nodeID: String): NodeRef = new RemoteNodeRef(nodeID)

    class RemoteNodeRef(nodeID: String) extends NodeRef {
      override def tell(msg: AnyRef): Unit = {
        val frame = Message(fromID = null, toID = nodeID, payload = msg)
        _handler.apply(frame)
      }

      override def ask(msg: AnyRef): Future[AnyRef] = ???
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
