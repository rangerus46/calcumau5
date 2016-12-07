package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener.CLOSE
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.worker.WorkerStore

class WorkerController(
  router: Router,
  workerStore: WorkerStore
) extends BaseController {

  router.get("/workers") { (ctx, _, _) =>
    val workers = workerStore.retrieveAll()
    val res = jsonHttpRes(content = workers)
    ctx.write(res).addListener(CLOSE)
  }

}
