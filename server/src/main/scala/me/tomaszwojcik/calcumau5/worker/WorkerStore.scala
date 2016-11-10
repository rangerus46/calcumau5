package me.tomaszwojcik.calcumau5.worker

import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

/**
  * Non-persistent in-memory worker store.
  */
class WorkerStore extends Logging {
  private val workersByAddress = new mutable.HashMap[String, Worker]

  def save(worker: Worker): Unit = synchronized {
    workersByAddress.remove(worker.address)
    workersByAddress.put(worker.address, worker)
  }

  def delete(worker: Worker): Option[Worker] = synchronized {
    workersByAddress.remove(worker.address)
  }
}
