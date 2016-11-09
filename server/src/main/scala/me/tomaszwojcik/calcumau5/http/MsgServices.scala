package me.tomaszwojcik.calcumau5.http

import java.io.InputStream

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.msg.{MsgCoder, MsgQueue}

class MsgServices(
  val msgQueue: MsgQueue,
  val msgCoder: MsgCoder) {

  def receive() = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = Future {
      req.withInputStream { is: InputStream =>
        msgCoder.decode(is)
      } map { msg: AnyRef =>
        msgQueue += msg
        Res(Ok)
      } getOrElse Res(BadRequest)
    }
  }
}
