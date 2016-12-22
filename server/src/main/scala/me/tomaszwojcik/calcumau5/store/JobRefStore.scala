package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.JobRef

trait JobRefStore
  extends BaseStore[JobRef, Long]

class SimpleJobRefStore
  extends JobRefStore
    with SimpleBaseStore[JobRef, Long]
    with LongIdGenerator {

  // FIXME: only there for test purposes
  add(JobRef(
    "me.tomaszwojcik.calcumau5.job.PingPongJob",
    "job-0.jar"
  ))

}
