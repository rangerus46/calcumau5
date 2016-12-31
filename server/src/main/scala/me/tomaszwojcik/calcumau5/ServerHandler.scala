package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.frames.{Frame, FrameHandler}
import me.tomaszwojcik.calcumau5.impl.NodeContextImpl
import me.tomaszwojcik.calcumau5.test.{PingNode, PongNode}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class ServerHandler
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  var channel: Channel = _
  val nodes = Seq(new test.PongNode, new test.PingNode)

  val pingNodeRef = new NodeRef {
    // FIXME: for tests only
    override def tell(msg: AnyRef) = frameHandler.apply(frames.Tell("ping-node", msg))

    override def ask(msg: AnyRef) = ???
  }

  val pongNodeRef = new NodeRef {
    // FIXME: for tests only
    override def tell(msg: AnyRef) = frameHandler.apply(frames.Tell("pong-node", msg))

    override def ask(msg: AnyRef) = ???
  }

  val frameHandler: FrameHandler = { frame: Frame =>
    channel.writeAndFlush(frame)
  }

  override def channelRead0(ctx: ChannelHandlerContext, frame: Frame): Unit = frame match {
    case frames.Ping => ctx.writeAndFlush(frames.Pong)
    case frames.Pong => // ignore pongs
    case frames.Start => handleStartFrame()
    case f: frames.Tell => handleTellFrame(f)
    case _ => log.info("Received frame: {}", frame)
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    channel = ctx.channel
    val address = channel.remoteAddress.asInstanceOf[InetSocketAddress]
    log.info(s"Accepted connection from ${address.getHostString}:${address.getPort}")
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    val address = channel.remoteAddress.asInstanceOf[InetSocketAddress]
    log.info(s"Client at ${address.getHostString}:${address.getPort} closed the connection")
  }

  private def handleStartFrame(): Unit = {
    nodes.map(_.ctx).foreach {
      case node: NodeContextImpl => node.swapHandler(frameHandler)
      case _ =>
    }
  }

  private def handleTellFrame(frame: frames.Tell): Unit = {
    // TODO: routing frames to recipient nodes instead of all nodes
    val msg = frame.msg

    // FIXME: for tests only
    nodes.filter(_.receive.isDefinedAt(msg)).foreach {
      case node: PingNode => node.withSender(pongNodeRef)(_.receive(msg))
      case node: PongNode => node.withSender(pingNodeRef)(_.receive(msg))
    }
  }

  private implicit class RichNode(node: Node) {
    def withSender[A](ref: NodeRef)(fn: Node => A): A = node synchronized {
      node.sender = ref
      val v = fn(node)
      node.sender = null
      v
    }
  }

}
