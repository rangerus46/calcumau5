package me.tomaszwojcik.calcumau5

import java.util.concurrent.Executors

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, ChannelOption, EventLoopGroup}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.Try

object Server extends Logging {

  implicit private val ec = {
    val es = Executors.newFixedThreadPool(ServerConf.Threading.MaxThreads)
    ExecutionContext.fromExecutorService(es)
  }

  private val parentEventLoopGroup: EventLoopGroup = new NioEventLoopGroup
  private val childEventLoopGroup: EventLoopGroup = new NioEventLoopGroup
  private val channelInitializer: ChannelInitializer[SocketChannel] = new ServerChannelInitializer

  private val bootstrap = new ServerBootstrap()
    .group(parentEventLoopGroup, childEventLoopGroup)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(channelInitializer)
    .option(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)

  def main(args: Array[String]): Unit = try {
    // Optionally print out an ascii art.
    asciiArt.map(log.info)

    val port = ServerConf.Tcp.Port
    val f = bootstrap.bind(port).sync()

    log.info("Started Calcumau5 server on port {}", port)
    f.channel().closeFuture().sync()
  } finally {
    parentEventLoopGroup.shutdownGracefully()
    childEventLoopGroup.shutdownGracefully()
  }

  lazy val asciiArt: Try[String] = Try {
    val is = getClass.getResourceAsStream("/ascii-art.txt")
    Source.fromInputStream(is, "UTF-8").mkString
  }

}
