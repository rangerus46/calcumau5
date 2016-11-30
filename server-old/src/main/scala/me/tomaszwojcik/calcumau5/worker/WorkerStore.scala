package me.tomaszwojcik.calcumau5.worker

import scala.collection.mutable

class WorkerStore {
  private lazy val workersByAddr = new mutable.HashMap[String, Worker]

  def save(worker: Worker): Worker = workersByAddr.synchronized {
    workersByAddr.remove(worker.address)
    workersByAddr.put(worker.address, worker)
    worker
  }

  def remove(worker: Worker): Option[Worker] = workersByAddr.synchronized {
    workersByAddr.remove(worker.address)
  }

  def findByAddress(addr: String): Option[Worker] = {
    workersByAddr.get(addr)
  }
}
