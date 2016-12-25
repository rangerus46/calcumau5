package me.tomaszwojcik.calcumau5

import java.io.File

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelInitializer, EventLoopGroup}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.io.Source
import scala.util.Try

object Server extends Logging {

  private val parentEventLoopGroup: EventLoopGroup = new NioEventLoopGroup
  private val childEventLoopGroup: EventLoopGroup = new NioEventLoopGroup
  private val channelInitializer: ChannelInitializer[SocketChannel] = new ServerChannelInitializer

  private val bootstrap = new ServerBootstrap()
    .group(parentEventLoopGroup, childEventLoopGroup)
    .channel(classOf[NioServerSocketChannel])
    .childHandler(channelInitializer)

  def main(args: Array[String]): Unit = try {
    // Optionally print out an ascii art.
    asciiArt.map(log.info)

    // Check if env variables are set before starting up the server.
    if (!validateEnvVariables()) {
      return
    }

    val f = bootstrap.bind(Conf.Http.Port).sync()
    log.info("Started Calcumau5 server on port {}", Conf.Http.Port)
    f.channel().closeFuture().sync()
  } finally {
    parentEventLoopGroup.shutdownGracefully()
    childEventLoopGroup.shutdownGracefully()
  }

  private def validateEnvVariables(): Boolean = {
    val msgPrefix = "Environment variable 'CALCUMAU5_HOME'"

    if (Conf.Env.Calcumau5HomePath == null) {
      log.error(s"$msgPrefix is not set: it should point to the Calcumau5 installation path")
      return false
    }

    val file = new File(Conf.Env.Calcumau5HomePath)
    if (!file.isDirectory) {
      log.error(s"$msgPrefix is not valid: directory '{}' does not exist or can't be read", file.getPath)
      false
    } else {
      log.info(s"$msgPrefix is set and points to '{}'", file.getPath)
      true
    }
  }

  lazy val asciiArt: Try[String] = Try {
    val is = getClass.getResourceAsStream("/ascii-art.txt")
    val source = Source.fromInputStream(is, "UTF-8")
    source.mkString
  }

}
