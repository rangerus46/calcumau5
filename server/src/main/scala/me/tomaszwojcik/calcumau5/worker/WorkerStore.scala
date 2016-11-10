package me.tomaszwojcik.calcumau5.worker

import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

/**
  * Non-persistent in-memory worker store.
  */
class WorkerStore extends Logging {
  private val workersByAddress = new mutable.HashMap[String, Worker]

  def register(worker: Worker): Unit = synchronized {
    workersByAddress.put(worker.address, worker)
  }

  def unregister(addr: String): Option[Worker] = synchronized {
    workersByAddress.remove(addr)
  }
}
