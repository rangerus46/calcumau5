package me.tomaszwojcik.calcumau5.handler

import java.nio.file.{Files, Path}

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext}
import me.tomaszwojcik.calcumau5.frames
import me.tomaszwojcik.calcumau5.frames.Frame
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

    val frame = frames.File(jarBytes)

    val listener = new ChannelFutureListener {
      override def operationComplete(future: ChannelFuture) = {
        log.info(s"Deploy to ${server.id}: finished")
        future.channel.close()
      }
    }

    ctx.writeAndFlush(frame).addListener(listener)
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit = {}

}
