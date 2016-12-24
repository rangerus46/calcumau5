package me.tomaszwojcik.calcumau5

import java.io.File

import com.softwaremill.macwire.wire
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.Future
import me.tomaszwojcik.calcumau5.client.HttpClient
import me.tomaszwojcik.calcumau5.controllers.{HealthController, JobController, WorkerController}
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.service.JobExecutorService
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.util.NettyConversions._
import slick.driver.HsqldbDriver.api._

import scala.io.Source
import scala.util.Try

object App extends Logging {

  def main(args: Array[String]): Unit = {
    // Optionally print out an ascii art.
    for (s <- asciiArt) {
      log.info(s)
    }

    // Check if env variables are set before starting up the server.
    if (!validateEnvVariables()) {
      return
    }

    val serverChannelInitializer = new ServerChannelInitializer(router)

    try {
      val bootstrap = new ServerBootstrap()
        .group(parentGroup, childGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(serverChannelInitializer)

      val f = bootstrap.bind(Conf.Http.Port)

      f.addListener { _: Future[Void] =>
        log.info("Started Calcumau5 server on port {}", Conf.Http.Port)
      }

      f.channel().closeFuture().sync()
    } finally {
      parentGroup.shutdownGracefully()
      childGroup.shutdownGracefully()
    }
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

  implicit val db = Database.forConfig("db")

  val parentGroup = new NioEventLoopGroup
  val childGroup = new NioEventLoopGroup

  val client = new HttpClient(parentGroup)

  lazy val router: Router = wire[Router]

  val workerController: WorkerController = wire[WorkerController]
  val healthController: HealthController = wire[HealthController]
  val jobController: JobController = wire[JobController]

  lazy val jobExecutorService: JobExecutorService = wire[JobExecutorService]

  Entities.init()

}
