package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandler, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.util.Logging

class ServerChannelInitializer extends ChannelInitializerBase with Logging {
  override def inboundHandler: ChannelInboundHandler = new SimpleChannelInboundHandler[String]() {
    override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
      log.info("Received message: {}", msg)
      if (msg == "PING") {
        log.info("Sending message: {}", "PONG")
        ctx.writeAndFlush("PONG")
      }
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
}

