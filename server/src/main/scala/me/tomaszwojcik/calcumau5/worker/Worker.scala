package me.tomaszwojcik.calcumau5.worker

import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.Future

case class Worker(address: String)
  extends StoreOperations with Logging {

  def checkIfAlive(): Future[Boolean] = Future.successful(true)
}

trait StoreOperations {
  this: Worker =>

  def save()(implicit store: WorkerStore): Worker = store.save(this)

  def remove()(implicit store: WorkerStore): Option[Worker] = store.remove(this)
}
