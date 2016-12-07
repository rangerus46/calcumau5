package me.tomaszwojcik.calcumau5

import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http._
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.util.Logging

class ServerChannelInitializer(router: Router) extends ChannelInitializer[SocketChannel] with Logging {

  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline
      .addLast(new HttpServerCodec)
      .addLast(new HttpObjectAggregator(10000000))
      .addLast(router)
  }

}
