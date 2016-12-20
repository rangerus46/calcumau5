package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.JobRef

trait JobRefStore
  extends BaseStore[JobRef, Long]

class SimpleJobRefStore
  extends JobRefStore
    with SimpleBaseStore[JobRef, Long]
    with LongIdGenerator {

  add(JobRef(1L, "me.tomaszwojcik.TestJob", "/home/Desktop/TestJob.jar"))

}
