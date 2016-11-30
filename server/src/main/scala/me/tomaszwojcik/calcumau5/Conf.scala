package me.tomaszwojcik.calcumau5

import com.typesafe.config.ConfigFactory

object Conf {
  private val config = ConfigFactory.defaultApplication()

  object Http {
    val Port: Int = config.getInt("http.port")
  }
}
