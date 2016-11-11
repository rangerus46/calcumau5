package me.tomaszwojcik.calcumau5.worker

import me.tomaszwojcik.calcumau5.Config
import me.tomaszwojcik.calcumau5.util.Logging
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.api.Response.CompleteListener
import org.eclipse.jetty.client.api.{Response, Result}
import org.eclipse.jetty.http.{HttpMethod, HttpStatus}

import scala.concurrent.{ExecutionContext, Future, Promise}

case class Worker(address: String)
  extends StoreOperations with Logging {

  def request(path: String)(implicit hc: HttpClient, ec: ExecutionContext): Future[Response] = {
    val promise = Promise[Response]
    hc.newRequest(address)
      .method(HttpMethod.GET)
      .path(path)
      .send(new CompleteListener {
        override def onComplete(result: Result): Unit = {
          if (result.isSucceeded) {
            promise.success(result.getResponse)
          } else {
            promise.failure(result.getFailure)
          }
        }
      })
    promise.future
  }

  def checkIfAlive()(implicit client: HttpClient, ec: ExecutionContext): Future[Boolean] = {
    request(Config.Health.PingPath).map { res: Response =>
      HttpStatus.isSuccess(res.getStatus)
    }
  }
}

trait StoreOperations {
  this: Worker =>

  def save()(implicit store: WorkerStore): Worker = store.save(this)

  def remove()(implicit store: WorkerStore): Option[Worker] = store.remove(this)
}
