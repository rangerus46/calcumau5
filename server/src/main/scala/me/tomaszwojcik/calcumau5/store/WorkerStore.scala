package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.Worker
import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

trait WorkerStore {
  def add(worker: Worker): Unit

  def remove(worker: Worker): Unit

  def retrieveAll(): List[Worker]
}

class SimpleWorkerStore extends WorkerStore with Logging {
  private val workers = new mutable.ArrayBuffer[Worker]

  override def add(worker: Worker): Unit = {
    workers += worker
  }

  override def remove(worker: Worker): Unit = {
    workers -= worker
  }

  override def retrieveAll(): List[Worker] = {
    workers.toList
  }
}
