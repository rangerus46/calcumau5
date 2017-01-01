package me.tomaszwojcik.calcumau5

import com.typesafe.config.{Config, ConfigFactory}
import me.tomaszwojcik.calcumau5.util.DurationConversions.toScala

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.duration.Duration

object ClientConf {

  private val config: Config = ConfigFactory.defaultApplication()

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

  case class Node(id: String, server: Server, className: String)

  case class Server(id: String, host: String, port: Int)

}
