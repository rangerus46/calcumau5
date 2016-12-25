package me.tomaszwojcik.calcumau5

import java.nio.charset.StandardCharsets

object Constants {

  val DefaultCharset = StandardCharsets.UTF_8

  object Network {
    val MaxFrameLength = 10000000
    val LengthFieldLength = 4
  }

}
