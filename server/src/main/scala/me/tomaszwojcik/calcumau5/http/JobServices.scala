package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future

class JobServices {
  def list() = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = Future {
      Res(Ok)
    }
  }

  def receive() = new Service[Req, Res] {
    override def apply(request: Req): Future[Res] = Future {
      Res(Created)
    }
  }
}
