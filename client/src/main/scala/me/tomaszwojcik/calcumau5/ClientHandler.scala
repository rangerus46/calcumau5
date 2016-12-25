package me.tomaszwojcik.calcumau5

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class ClientHandler extends SimpleChannelInboundHandler[String] with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    log.info("Received message: {}", msg)
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    log.info("Channel active")
    sendPingMessage(ctx)
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    log.info("Channel inactive")
  }

  private def sendPingMessage(ctx: ChannelHandlerContext): Unit = {
    val msg = "PING"
    log.info("Sending message: {}", msg)
    ctx.writeAndFlush(msg)
  }

}
