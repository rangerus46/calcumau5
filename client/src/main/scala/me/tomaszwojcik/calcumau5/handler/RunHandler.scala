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

    // Find nodes that have to be created on this server.
    val server = getServerAttr(ctx)
    val nodes = ClientConf.Nodes.filter(_.server == server)

    if (nodes.nonEmpty) {
      val classNamesByNodeID = nodes.map(n => (n.id, n.className)).toMap
      val frame = frames.Run(nodes = classNamesByNodeID)
      ctx.writeAndFlush(frame)
      log.info(s"Sent frame: $frame")
    }
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit = {
    val server = getServerAttr(ctx)
    log.info(s"Received $frame from $server")

    frame match {
      case f: frames.Message => handleMessageFrame(ctx, f)
      case frames.Disconnect => handleDisconnectFrame(ctx)
      case _ =>
    }
  }

  private def handleMessageFrame(ctx: ChannelHandlerContext, frame: frames.Message): Unit = {
    val to = nodeByID(frame.recipient)
    val matcher = new NodeChannelMatcher(to)

    log.info(s"Sent frame: $frame to $to")
    channels.writeAndFlush(frame, matcher)
  }

  private def handleDisconnectFrame(ctx: ChannelHandlerContext): Unit = {
    ctx.close()
  }

  private def nodeByID(id: String): Node = {
    ClientConf.Nodes.find(_.id == id).getOrElse(throw NodeNotFoundException(id))
  }

}
