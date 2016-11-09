package me.tomaszwojcik.calcumau5

import com.typesafe.config.ConfigFactory

object Config {
  val config = ConfigFactory.defaultApplication()

  object Http {
    val Port = config.getInt("http.port")
  }
}
