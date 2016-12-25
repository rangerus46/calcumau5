package me.tomaszwojcik.calcumau5

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.timeout.{IdleState, IdleStateEvent}
import me.tomaszwojcik.calcumau5.util.Logging

@Sharable
class ClientHandler extends SimpleChannelInboundHandler[String] with Logging {

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    log.info("Received message: {}", msg)
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    log.info("Channel active")
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    log.info("Channel inactive")
  }

  private def sendPingMessage(ctx: ChannelHandlerContext): Unit = {
    val msg = "PING"
    log.info("Sending message: {}", msg)
    ctx.writeAndFlush(msg)
  }

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: scala.Any): Unit = evt match {

    // Handle pauses in communication.
    case evt: IdleStateEvent => evt.state match {

      // Fired when nothing was written for some time.
      // Send a ping to ensure that the server is responding.
      case IdleState.WRITER_IDLE => sendPingMessage(ctx)

      // Fired when nothing was received from the server for some time.
      // Close the connection, since the server is not responding.
      case IdleState.READER_IDLE => ctx.close()

      case _ =>

    }

    case _ =>
  }
}
