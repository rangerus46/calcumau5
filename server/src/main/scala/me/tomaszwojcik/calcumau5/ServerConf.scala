package me.tomaszwojcik.calcumau5

import com.typesafe.config.ConfigFactory

object ServerConf {

  private val config = ConfigFactory.defaultApplication()

  object Tcp {
    val Port: Int = config.getInt("tcp.port")
  }

  object Threading {
    val MaxThreads: Int = config.getInt("threading.max-threads")
  }

}
