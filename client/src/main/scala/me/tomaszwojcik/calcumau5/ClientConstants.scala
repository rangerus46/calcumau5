package me.tomaszwojcik.calcumau5

import io.netty.util.AttributeKey
import me.tomaszwojcik.calcumau5.ClientConf.Server

object ClientConstants {
  val ServerAttr = AttributeKey.valueOf[Server]("server")
}
