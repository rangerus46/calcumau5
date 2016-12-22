package me.tomaszwojcik.calcumau5.job

trait Job {

  import Job._

  private var _status: Status = Started
  private val _sender: WorkerRef = new NoopWorkerRef

  def status: Status = _status

  def sender: WorkerRef = _sender

  def receive: PartialFunction[AnyRef, Unit]

  def finish(): Unit = _status match {
    case Started => _status = Finished
    case _ => throw new IllegalStateException("Job is already completed")
  }
}

object Job {

  sealed abstract class Status

  case object Started extends Status

  case object Failed extends Status

  case object Finished extends Status

}
