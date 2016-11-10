package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.worker.{Worker, WorkerStore}

import scala.io.Source
import scala.pickling.Unpickler
import scala.pickling.static._
import scala.pickling.Defaults._
import scala.pickling.json._

case class WorkerDto(address: String) {
  def toWorker = Worker(address, isConnected = false)
}

class WorkerServices(
  private val workerStore: WorkerStore) extends Logging {

  implicit val workerDtoUnpickler: Unpickler[WorkerDto] = Unpickler.generate[WorkerDto]

  def register(): Service[Req, Res] = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = {
      Future {
        req.withInputStream(Source.fromInputStream)
          .mkString
          .unpickle[WorkerDto]
      } flatMap {
        _.toWorker.checkConnection()
      } map { worker =>
        if (worker.isConnected) {
          log.info("Registering a new worker: {}", worker.toString)
          workerStore.save(worker)
          Res(Created)
        } else {
          log.error("Failed to connect to the worker at {}", worker.address)
          Res(BadRequest)
        }
      } handle {
        case e: Throwable =>
          log.error("Failed to create a worker", e)
          Res(BadRequest)
      }
    }
  }

}
