package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future

class MessageServices {
  def receive() = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = Future {
      Res(Ok)
    }
  }
}
