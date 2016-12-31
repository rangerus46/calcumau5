package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{Channel, ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.frames.{Frame, FrameHandler}
import me.tomaszwojcik.calcumau5.impl.NodeContextImpl
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class ServerHandler
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  var channel: Channel = _
  val nodes = Seq(new test.PongNode, new test.PingNode)

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
    for (node <- nodes if node.receive.isDefinedAt(frame)) {
      node.receive(frame)
    }
  }

}
