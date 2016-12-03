package me.tomaszwojcik.calcumau5

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http._
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.util.Logging

class ServerChannelInitializer extends ChannelInitializer[SocketChannel] with Logging {

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline
      .addLast(new HttpServerCodec)
      .addLast(new HttpObjectAggregator(10000000))
      .addLast(router)
  }

  private val router = new Router

  router.get("/items/:id") { (ctx, msg, vars) =>
    log.info("GET item with id: {}", vars("id").toInt)
    val res = new DefaultHttpResponse(msg.protocolVersion, HttpResponseStatus.OK)
    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
  }

  router.get("/items") { (ctx, msg, _) =>
    log.info("GET all items")
    val res = new DefaultHttpResponse(msg.protocolVersion, HttpResponseStatus.OK)
    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
  }

  router.post("/items") { (ctx, msg, _) =>
    log.info("POST an item")
    val res = new DefaultHttpResponse(msg.protocolVersion, HttpResponseStatus.OK)
    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
  }
}
