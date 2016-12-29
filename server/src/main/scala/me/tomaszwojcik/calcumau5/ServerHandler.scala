package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.frames.Frame
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class ServerHandler extends SimpleChannelInboundHandler[Frame] with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, frame: Frame): Unit = frame match {
    case frames.Ping => ctx.writeAndFlush(frames.Pong)
    case frames.Pong => // ignore pongs
    case _ => log.info("Received frame: {}", frame)
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    val address = ctx.channel().remoteAddress().asInstanceOf[InetSocketAddress]
    log.info(s"Accepted connection from ${address.getHostString}:${address.getPort}")
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    val address = ctx.channel().remoteAddress().asInstanceOf[InetSocketAddress]
    log.info(s"Client at ${address.getHostString}:${address.getPort} closed the connection")
  }

}
