package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.group.ChannelGroup
import me.tomaszwojcik.calcumau5.ClientConf.Node
import me.tomaszwojcik.calcumau5.exceptions.NodeNotFoundException
import me.tomaszwojcik.calcumau5.frames.Frame
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.{ClientConf, frames}

@Sharable
class RunHandler(channels: ChannelGroup)
  extends BaseClientHandler(channels)
    with Logging {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    super.channelActive(ctx)

    log.info(s"Sent frame: ${frames.Start}")
    ctx.writeAndFlush(frames.Start)
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit = {
    log.info("Received frame: {}", frame)
    frame match {
      case f: frames.Tell => handleTell(ctx, f)
      case f: frames.Ask => handleAsk(ctx, f)
    }
  }

  private def handleTell(ctx: ChannelHandlerContext, frame: frames.Tell): Unit = {
    val recipient: Node = nodeByID(frame.nodeID)
    val matcher = new NodeChannelMatcher(recipient)

    log.info(s"Sent frame: $frame to $recipient")
    channels.writeAndFlush(frame, matcher)
  }

  private def handleAsk(ctx: ChannelHandlerContext, frame: frames.Ask): Unit = ???

  private def nodeByID(id: String): Node = {
    ClientConf.Nodes.find(_.id == id).getOrElse(throw NodeNotFoundException(id))
  }

}
