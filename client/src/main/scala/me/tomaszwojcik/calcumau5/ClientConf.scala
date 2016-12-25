package me.tomaszwojcik.calcumau5

import com.typesafe.config.{Config, ConfigFactory}
import me.tomaszwojcik.calcumau5.util.DurationConversions.toScala

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.duration.Duration

object ClientConf {

  private val config: Config = ConfigFactory.defaultApplication()

  val Nodes = config.getConfigList("nodes").asScala.toList.map { conf: Config =>
    Node(host = conf.getString("host"), port = conf.getInt("port"))
  }

  object Tcp {
    val Timeout: Duration = config.getDuration("tcp.timeout")
    val PingInterval: Duration = config.getDuration("tcp.ping-interval")
  }

}
