package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandler, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.util.Logging

class ClientChannelInitializer extends ChannelInitializerBase with Logging {
  override def inboundHandler: ChannelInboundHandler = new SimpleChannelInboundHandler[String]() {
    override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
      log.info("Received message: {}", msg)
    }

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
      val address = ctx.channel().remoteAddress().asInstanceOf[InetSocketAddress]
      log.info(s"Connected to ${address.getHostString}:${address.getPort}")

      val msg = "PING"
      log.info("Sending message: {}", msg)
      ctx.writeAndFlush(msg)
    }
  }
}

