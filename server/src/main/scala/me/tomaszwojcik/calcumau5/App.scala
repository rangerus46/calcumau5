package me.tomaszwojcik.calcumau5

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.Future
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.util.NettyConversions._

object App extends Logging {
  def main(args: Array[String]): Unit = {
    val parentGroup = new NioEventLoopGroup
    val childGroup = new NioEventLoopGroup
    val serverChannelInitializer = new ServerChannelInitializer

    try {
      val bootstrap = new ServerBootstrap()
        .group(parentGroup, childGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(serverChannelInitializer)

      val f = bootstrap.bind(Conf.Http.Port)

      f.addListener { _: Future[Void] =>
        log.info("Started Calcumau5 server on port {}.", Conf.Http.Port)
      }

      f.channel().closeFuture().sync()
    } finally {
      parentGroup.shutdownGracefully()
      childGroup.shutdownGracefully()
    }
  }
}
