package me.tomaszwojcik.calcumau5.router

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, SimpleChannelInboundHandler, ChannelHandlerContext => CHCtx}
import io.netty.handler.codec.http._
import me.tomaszwojcik.calcumau5.router.Exceptions.NotFoundException
import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

@Sharable
class Router extends SimpleChannelInboundHandler[FullHttpRequest] with Logging {

  private val routes = new mutable.Queue[Route]

  private def register(m: HttpMethod, s: String)(fn: (CHCtx, FullHttpRequest, Map[String, String]) => Unit) = {
    routes += Route(m, s, fn)
    this
  }

  def get(path: String)(handler: (CHCtx, FullHttpRequest, Map[String, String]) => Unit): Router = {
    register(HttpMethod.GET, path)(handler)
  }

  def post(path: String)(handler: (CHCtx, FullHttpRequest, Map[String, String]) => Unit): Router = {
    register(HttpMethod.POST, path)(handler)
  }

  def put(path: String)(handler: (CHCtx, FullHttpRequest, Map[String, String]) => Unit): Router = {
    register(HttpMethod.PUT, path)(handler)
  }

  def delete(path: String)(handler: (CHCtx, FullHttpRequest, Map[String, String]) => Unit): Router = {
    register(HttpMethod.DELETE, path)(handler)
  }

  override def channelRead0(ctx: CHCtx, msg: FullHttpRequest): Unit = {
    val queryStringDecoder = new QueryStringDecoder(msg.uri)
    val path = queryStringDecoder.path

    routes.find(_.matches(msg.method, path)) match {
      case Some(route) => route.handler(ctx, msg, route extractVars path)
      case None => throw new NotFoundException(msg.method, path)
    }
  }

  override def channelReadComplete(ctx: CHCtx): Unit = ctx.flush()

  override def exceptionCaught(ctx: CHCtx, cause: Throwable): Unit = {
    import HttpResponseStatus._

    val res = cause match {
      case _: NotFoundException =>
        new DefaultHttpResponse(HttpVersion.HTTP_1_1, NOT_FOUND)

      case e: Exception =>
        log.error("Unhandled exception was caught", e)
        new DefaultHttpResponse(HttpVersion.HTTP_1_1, INTERNAL_SERVER_ERROR)
    }

    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
  }
}
