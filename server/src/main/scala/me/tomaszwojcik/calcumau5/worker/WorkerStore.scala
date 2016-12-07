package me.tomaszwojcik.calcumau5.worker

class WorkerStore {

  def add(worker: Worker): Unit = ???

  def remove(worker: Worker): Unit = ???

  def retrieveAll(): List[Worker] = List(Worker("192.168.0.1", 5555), Worker("192.168.0.2", 5555))

}
