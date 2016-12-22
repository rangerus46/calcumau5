package me.tomaszwojcik.calcumau5.job

import org.slf4j.LoggerFactory

class NoopWorkerRef extends WorkerRef {

  private val logger = LoggerFactory.getLogger(classOf[NoopWorkerRef])

  override def <(msg: AnyRef): Unit = logger.info("Received message: {}", msg)
}
