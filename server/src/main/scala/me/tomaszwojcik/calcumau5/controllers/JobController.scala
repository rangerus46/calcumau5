package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener
import io.netty.handler.codec.http.HttpResponseStatus
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.service.JobExecutorService
import me.tomaszwojcik.calcumau5.store.JobRefStore
import me.tomaszwojcik.calcumau5.util.Logging

class JobController(
  router: Router,
  jobRefStore: JobRefStore,
  jobExecutorService: JobExecutorService
) extends BaseController with Logging {

  import JobController._

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
    * POST /jobs/start
    *
    * Finds a job with a given name and starts it with a given args.
    */
  router.post("/jobs/start") { (ctx, req, _) =>
    val msg = fromJson[StartJobMsg](req.content)
    val res = jobRefStore.findByName(msg.name) match {
      case Some(ref) =>
        jobExecutorService.startJob(ref, Map.empty)
        httpRes(status = HttpResponseStatus.OK)
      case None =>
        httpRes(status = HttpResponseStatus.NOT_FOUND)
    }
    ctx.write(res).addListener(ChannelFutureListener.CLOSE)
  }

}

object JobController {

  case class StartJobMsg(
    name: String
  )

}
