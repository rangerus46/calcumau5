package me.tomaszwojcik.calcumau5.service

import me.tomaszwojcik.calcumau5.domain.JobRef
import me.tomaszwojcik.calcumau5.store.JobRefStore
import me.tomaszwojcik.calcumau5.util.Logging

class JobExecutorService(jobRefStore: JobRefStore) extends Logging {

  //  private var awaitingJobRefs = new mutable.Queue[JobRef]
  //  private val jobExecutions = new mutable.Queue[JobExecution]

  def executeJob(ref: JobRef): Unit = synchronized {
    //    val instance = new WordCounterDef
    //    val path = getClass.getResource("/test-file.txt").getFile
    //    instance.map("test-file.txt", new File(path))
    //    instance.results.groupBy(_.word).foreach { pair =>
    //      instance.reduce(pair._1, pair._2.map(_.count))
    //    }
  }

  //  private def createJobInstance(jobRef: JobRef): Job = {
  //    val file = new File(Conf.FS.WorkerJarsDir, jobRef.jarName)
  //    val loader = URLClassLoader.newInstance(Array(file.toURI.toURL), getClass.getClassLoader)
  //    val clazz = loader.loadClass(jobRef.className)
  //    clazz.newInstance().asInstanceOf[Job]
  //  }

}
