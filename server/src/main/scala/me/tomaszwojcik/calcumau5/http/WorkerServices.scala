package me.tomaszwojcik.calcumau5.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.util.Future
import me.tomaszwojcik.calcumau5.util.Logging
import me.tomaszwojcik.calcumau5.worker.{Worker, WorkerStore}

import scala.io.Source
import scala.pickling.Defaults._
import scala.pickling.Unpickler
import scala.pickling.json._

class WorkerServices(
  implicit workerStore: WorkerStore) extends Logging {

  import WorkerServices._

  def register(): Service[Req, Res] = Service.mk { req: Req =>
    Future {
      req // Write the request body to a string.
        .withInputStream(Source.fromInputStream).mkString

        // Create an instance of the worker from the string.
        .unpickle[WorkerJson].toWorker
    } flatMap {

      // Test connection before adding to workers.
      _.checkConnection() map { worker =>

        // If connection was established, save the worker in the store.
        if (worker.connected) {
          log.info("Registering a new worker: {}", worker.toString)
          worker.save()
          Res(Created)
        } else {
          log.error("Failed to connect to the worker at {}", worker.address)
          Res(BadRequest)
        }

      }
    } handle {
      case e: Throwable =>
        log.error("Failed to create a worker", e)
        Res(BadRequest)
    }
  }
}

object WorkerServices {
  implicit val workerJsonUnpickler: Unpickler[WorkerJson] = Unpickler.generate[WorkerJson]

  case class WorkerJson(address: String) {
    def toWorker = Worker(address, connected = false)
  }

}
