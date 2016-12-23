package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.JobRef

trait JobRefStore
  extends BaseStore[JobRef, Long] {

  def findByName(name: String): Option[JobRef]

}

class SimpleJobRefStore
  extends JobRefStore
    with SimpleBaseStore[JobRef, Long]
    with LongIdGenerator {

  add(JobRef(name = "countWords", className = "me.tomaszwojcik.calcumau5.WordCountingJob", jarName = "job-0.jar"))

  override def findByName(name: String): Option[JobRef] = retrieveAll().find(_.name == name)

}
