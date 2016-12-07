package me.tomaszwojcik.calcumau5.controllers

import java.nio.charset.StandardCharsets

import io.netty.buffer.Unpooled.copiedBuffer
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.handler.codec.http.HttpHeaderNames.{CONTENT_LENGTH, CONTENT_TYPE}
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http._
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

trait BaseController {

  private implicit val jsonFormat = Serialization.formats(NoTypeHints)

  def httpRes(
    version: HttpVersion = HttpVersion.HTTP_1_1,
    status: HttpResponseStatus = HttpResponseStatus.OK,
    buf: ByteBuf = Unpooled.buffer(0)
  ): FullHttpResponse = {
    val res = new DefaultFullHttpResponse(version, status, buf)

    res.headers()
      .add(CONTENT_LENGTH, buf.readableBytes())

    res
  }

  def jsonHttpRes(
    version: HttpVersion = HttpVersion.HTTP_1_1,
    status: HttpResponseStatus = HttpResponseStatus.OK,
    content: AnyRef = null
  ): FullHttpResponse = {
    val buf = copiedBuffer(write(content), StandardCharsets.UTF_8)
    val res = httpRes(version, status, buf)

    res.headers()
      .add(CONTENT_TYPE, APPLICATION_JSON)

    res
  }

}
