package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.ChannelHandlerContext
import me.tomaszwojcik.calcumau5.frames
import me.tomaszwojcik.calcumau5.util.Logging

class StartHandler
  extends BaseClientHandler
    with Logging {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    log.info("channelActive")
    ctx.writeAndFlush(frames.Start)
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: frames.Frame): Unit = frame match {
    case tell: frames.Tell => log.info("Tell frame received: {}", tell)
    case ask: frames.Ask => log.info("Ask frame received: {}", ask)
  }

}
