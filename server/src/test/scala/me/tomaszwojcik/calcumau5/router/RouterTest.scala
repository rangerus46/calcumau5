package me.tomaszwojcik.calcumau5.router

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext => CHCtx}
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.http._
import org.scalatest.{FlatSpec, Matchers}

class RouterTest extends FlatSpec with Matchers {

  private def constantResponseHandler(status: HttpResponseStatus)
    (ctx: CHCtx, msg: FullHttpRequest, vars: Map[String, String]): Unit = {

    val res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status)
    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
  }

  "A Router" should "ignore trailing slashes in routes" in {
    val router = new Router().get("/items/")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items"))
      channel.readOutbound[HttpResponse].status
    }

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  it should "ignore trailing slashes in requests" in {
    val router = new Router().get("/items")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items"))
      channel.readOutbound[HttpResponse].status
    }

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  "A Router with no routes" should "respond with 404" in {
    val router = new Router

    assertResult(HttpResponseStatus.NOT_FOUND) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  "A Router with GET route" should "respond with 404 for POST request" in {
    val router = new Router().get("/")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.NOT_FOUND) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  it should "not respond with 404 for GET request" in {
    val router = new Router().get("/")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  "A Router with '/' path" should "respond with 404 for '/items' request" in {
    val router = new Router().get("/")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.NOT_FOUND) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items"))
      channel.readOutbound[HttpResponse].status
    }
  }

  it should "not respond with 404 for '/' request" in {
    val router = new Router().get("/")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  "A Router with '/items' path" should "respond with 404 for '/' request" in {
    val router = new Router().get("/items")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.NOT_FOUND) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"))
      channel.readOutbound[HttpResponse].status
    }
  }

  it should "not respond with 404 for '/items' request" in {
    val router = new Router().get("/items")(constantResponseHandler(HttpResponseStatus.OK))

    assertResult(HttpResponseStatus.OK) {
      val channel = new EmbeddedChannel(router)
      channel.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/items"))
      channel.readOutbound[HttpResponse].status
    }
  }

}
