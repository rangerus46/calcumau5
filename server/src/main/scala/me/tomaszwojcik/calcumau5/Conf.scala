package me.tomaszwojcik.calcumau5

import com.typesafe.config.ConfigFactory

object Conf {
  private val config = ConfigFactory.defaultApplication()

  object Http {
    val Port: Int = config.getInt("http.port")
    val MaxContentLength: Int = config.getMemorySize("http.max-content-length").toBytes.toInt
  }

  object Env {
    val Calcumau5HomePath: String = System.getenv("CALCUMAU5_HOME")
  }
}
