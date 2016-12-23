package me.tomaszwojcik.calcumau5.domain

sealed abstract class JobStatus

object JobStatus {

  case object Idle extends JobStatus

  case object Running extends JobStatus

  case object Finished extends JobStatus

  case object Failed extends JobStatus

}
