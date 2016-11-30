package me.tomaszwojcik.calcumau5

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http._
import org.slf4j.LoggerFactory

class HttpChannelInitializer extends ChannelInitializer[SocketChannel] {

  private val logger = LoggerFactory.getLogger(classOf[HttpChannelInitializer])

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline
      .addLast(new HttpRequestDecoder)
      .addLast(new HttpResponseEncoder)
      .addLast(channelInboundHandler)
  }

  def channelInboundHandler: ChannelInboundHandler = new SimpleChannelInboundHandler[HttpObject]() {
    override def channelRead0(ctx: ChannelHandlerContext, msg: HttpObject): Unit = msg match {
      case req: HttpRequest =>
        logger.info("Received HTTP request: {}", req.toString)
        val cf = ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
        cf.addListener(ChannelFutureListener.CLOSE)
      case content: HttpContent =>
        logger.info("Received HTTP content: {}", content.toString)
      case _ =>
        logger.error("Unknown message type: {}", msg.getClass.getName)
    }

    override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
      ctx.flush()
    }
  }
}
