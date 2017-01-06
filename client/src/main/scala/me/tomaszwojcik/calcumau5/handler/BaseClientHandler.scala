package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.timeout.{IdleState, IdleStateEvent}
import me.tomaszwojcik.calcumau5.ClientConf.Server
import me.tomaszwojcik.calcumau5.ClientConstants
import me.tomaszwojcik.calcumau5.frames.{Frame, PingFrame, PongFrame}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
abstract class BaseClientHandler(channels: ChannelGroup)
  extends SimpleChannelInboundHandler[Frame]
    with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, frame: Frame): Unit = frame match {
    case PingFrame => ctx.writeAndFlush(PongFrame)
    case PongFrame => // ignore pongs
    case _ => channelRead1(ctx, frame)
  }

  def channelRead1(ctx: ChannelHandlerContext, frame: Frame): Unit

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any): Unit = evt match {

    // Handle pauses in communication.
    case evt: IdleStateEvent => evt.state match {

      // Fired when nothing was written for some time.
      // Send a ping to ensure that the server is responding.
      case IdleState.WRITER_IDLE => ctx.writeAndFlush(PingFrame)

      // Fired when nothing was received from the server for some time.
      // Close the connection, since the server is not responding.
      case IdleState.READER_IDLE =>
        val server = getServerAttr(ctx)
        log.warn(s"Server ${server.id} at ${server.host}:${server.port} does not respond: closing connection")
        ctx.close()

      case _ =>

    }

    case _ =>
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = channels.add(ctx.channel)

  def getServerAttr(ctx: ChannelHandlerContext): Server = {
    val channel = ctx.channel()
    val attr = channel.attr(ClientConstants.ServerAttr)
    attr.get()
  }

}
