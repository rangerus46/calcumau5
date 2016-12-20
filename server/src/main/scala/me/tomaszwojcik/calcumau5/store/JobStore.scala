package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.JobRef

trait JobStore extends BaseStore[JobRef]

class SimpleJobStore extends JobStore with SimpleBaseStore[JobRef] {
  add(JobRef(1L, "me.tomaszwojcik.TestJob", "/home/Desktop/TestJob.jar"))
}
