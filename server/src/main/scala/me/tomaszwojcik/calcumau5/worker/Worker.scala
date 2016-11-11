package me.tomaszwojcik.calcumau5.worker

import com.twitter.finagle.http.Status.Successful
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.http.HealthServices

case class Worker(
  address: String,
  isConnected: Boolean) {

  def mkService[A](closure: Service[Req, Res] => Future[A]): Future[A] = {
    val service = Http.newService(address)
    closure(service).ensure {
      service.close()
    }
  }

  def checkConnection(): Future[Worker] = mkService { service =>
    service(HealthServices.PingReq) map {
      _.status match {
        case Successful(_) => copy(isConnected = true)
      }
    } handle {
      case _ => copy(isConnected = false)
    }
  }
}
