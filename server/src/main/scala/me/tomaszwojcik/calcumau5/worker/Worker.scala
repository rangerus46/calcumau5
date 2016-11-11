package me.tomaszwojcik.calcumau5.worker

import com.twitter.finagle.http.Status.Successful
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.http.HealthServices

import scala.collection.mutable

class Worker private(
  val saved: Boolean,
  val address: String,
  val connected: Boolean) {

  import Worker._

  def save(): Unit = workersByAddr.synchronized {
    workersByAddr.remove(address)
    if (saved) {
      workersByAddr.put(address, this)
    } else {
      workersByAddr.put(address, new Worker(saved = true, address, connected))
    }
  }

  def delete(): Option[Worker] = workersByAddr.synchronized {
    workersByAddr.remove(address)
  }

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

  def copy(
    address: String = address,
    connected: Boolean = connected): Worker = {

    new Worker(saved = false, address, connected)
  }
}

object Worker {
  private lazy val workersByAddr = new mutable.HashMap[String, Worker]

  def apply(address: String, connected: Boolean): Worker = {
    new Worker(saved = false, address, connected)
  }

  def byAddress(addr: String): Option[Worker] = workersByAddr.synchronized {
    workersByAddr.get(addr)
  }
}

