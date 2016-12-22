package me.tomaszwojcik.calcumau5.service

import java.io.File
import java.net.URLClassLoader

import me.tomaszwojcik.calcumau5.Conf
import me.tomaszwojcik.calcumau5.domain.JobRef
import me.tomaszwojcik.calcumau5.job.{ControlMessages, Job}
import me.tomaszwojcik.calcumau5.store.JobRefStore

import scala.collection.mutable

class JobExecutorService(
  jobRefStore: JobRefStore
) {

  private var awaitingJobRefs = new mutable.Queue[JobRef]
  private val startedJobs = new mutable.Queue[Job]

  def executeJob(ref: JobRef): Unit = synchronized {
    if (startedJobs.isEmpty) {
      val job = createJobInstance(ref)
      job.receive(ControlMessages.Begin)
      startedJobs += job
    } else {
      awaitingJobRefs += ref
    }
  }

  private def createJobInstance(jobRef: JobRef): Job = {
    val file = new File(Conf.FS.WorkerJarsDir, jobRef.jarName)
    val loader = URLClassLoader.newInstance(Array(file.toURI.toURL), getClass.getClassLoader)
    val clazz = loader.loadClass(jobRef.className)
    clazz.newInstance().asInstanceOf[Job]
  }

}
