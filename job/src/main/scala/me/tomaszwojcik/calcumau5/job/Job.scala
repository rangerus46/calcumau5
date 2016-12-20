package me.tomaszwojcik.calcumau5.job

trait Job {

  val sender: WorkerRef = null

  def init(): Unit

  def process(msg: AnyRef): Unit
}
