package me.tomaszwojcik.calcumau5

import com.softwaremill.macwire.wire
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.Future
import me.tomaszwojcik.calcumau5.client.HttpClient
import me.tomaszwojcik.calcumau5.controllers.WorkerController
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.store.{SimpleWorkerStore, WorkerStore}
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.util.NettyConversions._

import scala.io.Source
import scala.util.Try

object App extends Logging {

  def main(args: Array[String]): Unit = {
    val serverChannelInitializer = new ServerChannelInitializer(router)

    // Optionally print out an ascii art.
    for (s <- asciiArt) {
      log.info(s)
    }

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

  lazy val asciiArt: Try[String] = Try {
    val is = getClass.getResourceAsStream("/ascii-art.txt")
    val source = Source.fromInputStream(is, "UTF-8")
    source.mkString
  }

  val parentGroup = new NioEventLoopGroup
  val childGroup = new NioEventLoopGroup

  val client = new HttpClient(parentGroup)

  lazy val router: Router = wire[Router]

  val workerController: WorkerController = wire[WorkerController]
  lazy val workerStore: WorkerStore = wire[SimpleWorkerStore]

}
