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

class WorkerServices(
  private val workerStore: WorkerStore) extends Logging {

  implicit val workerUnpickler: Unpickler[Worker] = Unpickler.generate[Worker]

  def register(): Service[Req, Res] = new Service[Req, Res] {
    override def apply(req: Req): Future[Res] = Future {
      try {
        val worker = req
          .withInputStream(Source.fromInputStream)
          .mkString
          .unpickle[Worker]

        workerStore.register(worker)

        log.info("Registering a new worker: {}", worker.toString)
        Res(Created)
      } catch {
        case e: Exception =>
          log.error("Failed to create a worker", e)
          Res(BadRequest)
      }
    }
  }

}
