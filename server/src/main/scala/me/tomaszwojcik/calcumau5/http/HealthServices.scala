package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.Get
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future

class HealthServices {
  import HealthServices._

  def respondToPing(): Service[Req, Res] = new Service[Req, Res] {
    override def apply(request: Req): Future[Res] = Future.value(PongRes)
  }
}

object HealthServices {
  def PingReq = Req(Get, "/health/ping")
  def PongRes = Res(Ok)
}
