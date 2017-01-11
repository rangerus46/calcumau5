package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.group.ChannelGroup
import me.tomaszwojcik.calcumau5.ClientConf
import me.tomaszwojcik.calcumau5.ClientConf.Node
import me.tomaszwojcik.calcumau5.exceptions.NodeNotFoundException
import me.tomaszwojcik.calcumau5.frames.{Frame, LogFrame, MsgFrame, RunFrame}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class RunHandler(channels: ChannelGroup)(implicit conf: ClientConf)
  extends BaseClientHandler(channels)
    with Logging {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    super.channelActive(ctx)

    // Find nodes that have to be created on this server.
    val server = getServerAttr(ctx)
    val nodes = conf.Nodes.filter(_.server == server)

    if (nodes.nonEmpty) {
      val classNamesByNodeID = nodes.map(n => (n.id, n.className)).toMap
      val frame = RunFrame(nodes = classNamesByNodeID)
      ctx.writeAndFlush(frame)
    }
  }

  override def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit = {
    frame match {
      case msg: MsgFrame =>
        val to = nodeByID(msg.recipient)
        val matcher = new NodeChannelMatcher(to)
        channels.writeAndFlush(frame, matcher)

      case LogFrame(s, from) =>
        log.info(s"[$from] $s")

      case frame: Frame =>
        log.warn(s"Unknown frame: $frame")
    }
  }

  private def nodeByID(id: String): Node = {
    conf.Nodes.find(_.id == id).getOrElse(throw NodeNotFoundException(id))
  }

}
