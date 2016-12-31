package me.tomaszwojcik.calcumau5.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.group.ChannelGroup
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.timeout.{IdleState, IdleStateEvent}
import me.tomaszwojcik.calcumau5.frames
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
abstract class BaseClientHandler(channels: ChannelGroup)
  extends SimpleChannelInboundHandler[frames.Frame]
    with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, frame: frames.Frame): Unit = frame match {
    case frames.Ping => ctx.writeAndFlush(frames.Pong)
    case frames.Pong => // ignore pongs
    case _ => channelRead1(ctx, frame)
  }

  def channelRead1(ctx: ChannelHandlerContext, frame: frames.Frame): Unit

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: scala.Any): Unit = evt match {

    // Handle pauses in communication.
    case evt: IdleStateEvent => evt.state match {

      // Fired when nothing was written for some time.
      // Send a ping to ensure that the server is responding.
      case IdleState.WRITER_IDLE => ctx.writeAndFlush(frames.Ping)

      // Fired when nothing was received from the server for some time.
      // Close the connection, since the server is not responding.
      case IdleState.READER_IDLE => ctx.close()

      case _ =>

    }

    case _ =>
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    channels.add(ctx.channel)
  }

}
