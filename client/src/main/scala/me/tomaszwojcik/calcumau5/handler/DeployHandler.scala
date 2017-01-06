package me.tomaszwojcik.calcumau5.handler

import java.nio.file.{Files, Path}

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext}
import me.tomaszwojcik.calcumau5.frames.{FileFrame, Frame}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class DeployHandler(channels: ChannelGroup, jarPath: Path)
  extends BaseClientHandler(channels)
    with Logging {

  val jarBytes: Array[Byte] = Files.readAllBytes(jarPath)

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    super.channelActive(ctx)

    val server = getServerAttr(ctx)

    log.info(s"Deploy to ${server.id}: started")

    val frame = FileFrame(jarBytes)

    val f = ctx.writeAndFlush(frame)

    f.addListener(new ChannelFutureListener {
      override def operationComplete(future: ChannelFuture) = {
        log.info(s"Deploy to ${server.id}: finished")
      }
    })
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit = {}

}
