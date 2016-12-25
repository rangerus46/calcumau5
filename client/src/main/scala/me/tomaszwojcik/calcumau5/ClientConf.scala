package me.tomaszwojcik.calcumau5

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters.asScalaBufferConverter

object ClientConf {

  private val config: Config = ConfigFactory.defaultApplication()

  val Nodes = config.getConfigList("nodes").asScala.toList.map { conf: Config =>
    Node(host = conf.getString("host"), port = conf.getInt("port"))
  }
}
