package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.store.JobStore
import me.tomaszwojcik.calcumau5.util.Logging

class JobController(
  router: Router,
  jobStore: JobStore
) extends BaseController with Logging {

  /**
    * GET /jobs
    *
    * Retrieves all jobs present in the store.
    */
  router.get("/jobs") { (ctx, _, _) =>
    val jobs = jobStore.retrieveAll()
    val res = jsonHttpRes(content = jobs)
    ctx.write(res).addListener(ChannelFutureListener.CLOSE)
  }

}
