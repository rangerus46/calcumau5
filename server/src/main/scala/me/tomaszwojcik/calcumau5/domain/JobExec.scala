package me.tomaszwojcik.calcumau5.domain

import java.time.LocalDateTime

import me.tomaszwojcik.calcumau5.Job
import me.tomaszwojcik.calcumau5.Job.Args

case class JobExec(
  job: Job,
  args: Args = Map.empty,
  startDate: LocalDateTime = LocalDateTime.now(),
  endDate: LocalDateTime = null,
  status: JobStatus = JobStatus.Idle
) {
  def isCompleted: Boolean = {
    status == JobStatus.Finished || status == JobStatus.Failed
  }
}
