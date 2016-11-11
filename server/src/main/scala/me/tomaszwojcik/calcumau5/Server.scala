package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import com.twitter.app.App
import com.twitter.finagle.Http
import com.twitter.util.Await
import me.tomaszwojcik.calcumau5.http.ServicesModule
import me.tomaszwojcik.calcumau5.msg.MsgModule

object Server extends App
  with ServicesModule
  with MsgModule {

  def main(): Unit = {
    val address = new InetSocketAddress(Config.Http.Port)
    val server = Http.serve(address, routingService)
    Await.ready(server)
  }
}
