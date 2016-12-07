package me.tomaszwojcik.calcumau5.controllers

import io.netty.channel.ChannelFutureListener.CLOSE
import io.netty.handler.codec.http.HttpResponseStatus
import me.tomaszwojcik.calcumau5.domain.Worker
import me.tomaszwojcik.calcumau5.router.Router
import me.tomaszwojcik.calcumau5.store.WorkerStore

class WorkerController(
  router: Router,
  workerStore: WorkerStore
) extends BaseController {

  router.get("/workers") { (ctx, _, _) =>
    val workers = workerStore.retrieveAll()
    val res = jsonHttpRes(content = workers)
    ctx.write(res).addListener(CLOSE)
  }

  router.post("/workers") { (ctx, msg, _) =>
    val form = fromJson[WorkerForm](msg.content)
    workerStore.add(form.toWorker)

    val res = httpRes(status = HttpResponseStatus.CREATED)
    ctx.write(res).addListener(CLOSE)
  }

}

case class WorkerForm(host: String, port: Int) {
  def toWorker: Worker = new Worker(host, port)
}
