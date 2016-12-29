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

  override def channelRead1(ctx: ChannelHandlerContext, msg: frames.Frame): Unit = {
    log.info("Received frame: {}", msg)
  }

}
