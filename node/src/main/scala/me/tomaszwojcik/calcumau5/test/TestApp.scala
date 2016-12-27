package me.tomaszwojcik.calcumau5.test

import me.tomaszwojcik.calcumau5.NodeContextImpl
import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.frames.{GetNodeFrame, NewNodeFrame, TellFrame}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.Future

object TestApp extends Logging {
  def main(args: Array[String]): Unit = {
    val pingNode = new PingNode
    val pongNode = new PongNode

    val pingCtx = pingNode.ctx.asInstanceOf[NodeContextImpl]

    pingCtx.setFrameHandler {
      case GetNodeFrame(ref) =>
        log.info(s"GetNodeFrame: ref = ${ref.toString}")

      case NewNodeFrame(ref) =>
        log.info(s"NewNodeFrame: ref = ${ref.toString}")

      case TellFrame(ref, msg) =>
        log.info(s"TellFrame: ref = ${ref.toString}, msg = ${msg.toString}")
        pongNode.sender = new WrapperNodeRef(pingNode)
        pongNode.receive(msg)
        pongNode.sender = NoopNodeRef
    }

  }

  class WrapperNodeRef(node: Node) extends NodeRef {
    override def tell(msg: AnyRef): Unit = node.receive(msg)

    override def ask(msg: AnyRef): Future[AnyRef] = ???
  }

  object NoopNodeRef extends NodeRef {
    override def tell(msg: AnyRef): Unit = {}

    override def ask(msg: AnyRef): Future[AnyRef] = ???
  }

}
