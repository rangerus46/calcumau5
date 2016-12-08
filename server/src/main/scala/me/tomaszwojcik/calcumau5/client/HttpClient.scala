package me.tomaszwojcik.calcumau5.client

import java.nio.charset.StandardCharsets

import io.netty.bootstrap.Bootstrap
import io.netty.channel.socket.SocketChannel
import io.netty.channel._
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http._
import me.tomaszwojcik.calcumau5.Conf
import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.{Future, Promise}

class HttpClient(group: EventLoopGroup) extends Logging {

  private val bootstrap = new Bootstrap().group(group).channel(classOf[NioSocketChannel])

  class ClientChannelInitializer(handler: ChannelHandler) extends ChannelInitializer[SocketChannel] {
    override def initChannel(ch: SocketChannel): Unit = {
      ch.pipeline()
        .addLast(new HttpClientCodec())
        .addLast(new HttpObjectAggregator(Conf.Http.MaxContentLength))
        .addLast(handler)
    }
  }

  def send(req: HttpReq): Future[HttpRes] = {
    val promise = Promise[HttpRes]

    val inboundHandler = new SimpleChannelInboundHandler[FullHttpResponse] {
      override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpResponse): Unit = {
        val body = msg.content.toString(StandardCharsets.UTF_8)
        val res = HttpRes(body, msg.status)
        promise.success(res)
        ctx.close()
      }

      override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
        case e: Exception =>
          promise.failure(e)
          ctx.close()
        case _ => throw cause
      }
    }

    val channelFutureListener = new ChannelFutureListener {
      override def operationComplete(future: ChannelFuture): Unit = {
        if (future.isSuccess) {
          val channel = future.channel()
          val msg = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, req.method, req.uri)
          channel.writeAndFlush(msg)
        } else {
          promise.failure(future.cause)
        }
      }
    }

    val channelInitializer = new ClientChannelInitializer(inboundHandler)

    bootstrap
      .handler(channelInitializer)
      .connect(req.host, req.port)
      .addListener(channelFutureListener)

    promise.future
  }

}

case class HttpReq(
  host: String,
  port: Int,
  uri: String,
  method: HttpMethod = HttpMethod.GET
)

case class HttpRes(
  body: String,
  status: HttpResponseStatus
)
