package me.tomaszwojcik.calcumau5

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import me.tomaszwojcik.calcumau5.util.DurationConversions.toScala

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.duration.Duration

class ClientConf(path: Option[String]) {

  import ClientConf._

  private lazy val default = ConfigFactory.defaultApplication()

  private lazy val config: Config = path match {
    case Some(p) => ConfigFactory.parseFile(new File(p)).withFallback(default)
    case None => default
  }

  val Servers = config.getConfigList("servers").asScala.toList.map { conf =>
    Server(conf.getString("id"), conf.getString("host"), conf.getInt("port"))
  }

  val Nodes = config.getConfigList("nodes").asScala.toList.map { conf: Config =>
    val serverID = conf.getString("server")
    val server = Servers.find(_.id == serverID).get
    val className = conf.getString("class")
    Node(conf.getString("id"), server, className)
  }

  object Tcp {
    val Timeout: Duration = config.getDuration("tcp.timeout")
    val PingInterval: Duration = config.getDuration("tcp.ping-interval")
  }

}

object ClientConf {

  case class Node(id: String, server: Server, className: String)

  case class Server(id: String, host: String, port: Int)

}
