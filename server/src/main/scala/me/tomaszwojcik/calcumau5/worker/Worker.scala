package me.tomaszwojcik.calcumau5.worker

import com.twitter.finagle.http.Status.Successful
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.http.HealthServices
import me.tomaszwojcik.calcumau5.util.Logging

case class Worker(address: String, connected: Boolean)
  extends StoreOperations with Logging {

  def createService(): Service[Req, Res] = Http.newService(address)

  def checkConnection(): Future[Worker] = {
    val service = createService()
    service(HealthServices.PingReq) map {
      _.status match {
        case Successful(_) => copy(connected = true)
        case _ => copy(connected = false)
      }
    } handle {
      case _ => copy(connected = false)
    } ensure {
      service.close()
    }
  }
}

trait StoreOperations {
  this: Worker =>

  def save()(implicit store: WorkerStore): Worker = store.save(this)

  def remove()(implicit store: WorkerStore): Option[Worker] = store.remove(this)
}
