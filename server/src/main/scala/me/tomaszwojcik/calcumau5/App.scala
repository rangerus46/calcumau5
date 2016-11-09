package me.tomaszwojcik.calcumau5

import java.net.InetSocketAddress

import com.twitter.finagle.Http
import com.twitter.util.Await
import me.tomaszwojcik.calcumau5.http.ServicesModule

object App extends ServicesModule {
  def main(args: Array[String]): Unit = {
    val address = new InetSocketAddress(Config.Http.Port)
    val server = Http.serve(address, routingService)
    Await.ready(server)
  }
}
