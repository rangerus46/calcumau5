package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.msg.{MsgCoder, MsgQueue}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.util.{Failure, Success}

class MsgServices(
  val msgQueue: MsgQueue,
  val msgCoder: MsgCoder) extends Logging {

  def receive() = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = Future {
      req.withInputStream {
        msgCoder.decode
      } match {
        case Success(msg) =>
          log.info("Received a message: {}", msg.toString)
          msgQueue += msg
          Res(Ok)
        case Failure(e) =>
          log.error("Failed to decode a message", e)
          Res(BadRequest)
      }
    }
  }
}
