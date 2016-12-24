package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener
import io.netty.handler.codec.http.HttpResponseStatus._
import me.tomaszwojcik.calcumau5.Entities.jobRefs
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.service.JobExecutorService
import me.tomaszwojcik.calcumau5.util.Logging
import slick.driver.HsqldbDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

class JobController(
  db: Database,
  router: Router,
  jobExecutorService: JobExecutorService
) extends BaseController with Logging {

  import JobController._

  /**
    * GET /jobs
    *
    * Retrieves all jobs present in the store.
    */
  router.get("/jobs") { (ctx, _, _) =>
    val query = jobRefs.result
    val f = db.run(query)

    f.onSuccess {
      case jobs =>
        val res = jsonHttpRes(content = jobs)
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
    }
  }

  /**
    * POST /jobs/start
    *
    * Finds a job with a given name and starts it with a given args.
    */
  router.post("/jobs/start") { (ctx, req, _) =>
    val msg = fromJson[StartJobMsg](req.content)

    val query = jobRefs.filter(_.name === msg.name).result.headOption
    val f = db.run(query)

    f.onSuccess {
      case Some(job) =>
        jobExecutorService.startJob(job, Map.empty)
        val res = httpRes(status = OK)
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)

      case None =>
        val res = httpRes(status = NOT_FOUND)
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE)
    }
  }

}

object JobController {

  case class StartJobMsg(
    name: String
  )

}
