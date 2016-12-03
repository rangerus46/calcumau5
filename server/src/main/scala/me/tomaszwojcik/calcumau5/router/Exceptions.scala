package me.tomaszwojcik.calcumau5.router

import io.netty.handler.codec.http.HttpMethod

object Exceptions {

  class NotFoundException(val method: HttpMethod, val path: String) extends RuntimeException

}
