package me.tomaszwojcik.calcumau5.controllers

import java.io.File
import java.net.URLClassLoader

import io.netty.channel.ChannelFutureListener
import io.netty.handler.codec.http.HttpResponseStatus
import me.tomaszwojcik.calcumau5.Conf
import me.tomaszwojcik.calcumau5.domain.JobRef
import me.tomaszwojcik.calcumau5.job.Job
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.store.JobRefStore
import me.tomaszwojcik.calcumau5.util.Logging

class JobController(
  router: Router,
  jobRefStore: JobRefStore
) extends BaseController with Logging {

  /**
    * GET /jobs
    *
    * Retrieves all jobs present in the store.
    */
  router.get("/jobs") { (ctx, _, _) =>
    val jobs = jobRefStore.retrieveAll()
    val res = jsonHttpRes(content = jobs)
    ctx.write(res).addListener(ChannelFutureListener.CLOSE)
  }

  /**
    * POST /jobs/:id/start
    *
    * Starts job with a given id.
    */
  router.post("/jobs/:id/start") { (ctx, _, vars) =>
    val id = vars.get("id").map(_.toLong).get
    val res = jobRefStore.findById(id) match {
      case Some(jobRef) =>
        startJob(jobRef)
        httpRes(status = HttpResponseStatus.OK)
      case None =>
        httpRes(status = HttpResponseStatus.NOT_FOUND)
    }
    ctx.write(res).addListener(ChannelFutureListener.CLOSE)
  }

  private def startJob(jobRef: JobRef): Unit = {
    log.info("Starting job {}", jobRef)

    // TODO: add checks
    val file = new File(Conf.FS.WorkerJarsDir, jobRef.jarName)

    val loader = URLClassLoader.newInstance(Array(file.toURI.toURL), getClass.getClassLoader)

    val clazz = loader.loadClass(jobRef.className)
    val job = clazz.newInstance().asInstanceOf[Job]
    job.receive("PING")
  }

}
