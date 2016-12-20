package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.util.Logging

class HealthController(
  router: Router
) extends BaseController with Logging {

  /**
    * GET /ping
    *
    * Responds with an empty '200 OK' response allowing to check if the server is alive.
    */
  router.get("/ping") { (ctx, _, _) =>
    log.info("Received a ping")
    val res = httpRes()
    ctx.write(res).addListener(ChannelFutureListener.CLOSE)
  }

}
