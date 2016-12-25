package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import me.tomaszwojcik.calcumau5.util.Logging

object Client extends Logging {
  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup
    try {
      val b = new Bootstrap()
        .group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new ClientChannelInitializer)

      val f = b.connect("localhost", 5555)
      f.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully()
    }
  }
}
