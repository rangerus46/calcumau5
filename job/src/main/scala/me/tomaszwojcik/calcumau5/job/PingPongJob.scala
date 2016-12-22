package me.tomaszwojcik.calcumau5.job

import org.slf4j.LoggerFactory

class PingPongJob extends Job {

  private val logger = LoggerFactory.getLogger(classOf[PingPongJob])
  logger.info("Job status: {}", status)

  override def receive: PartialFunction[AnyRef, Unit] = {
    case msg =>
      logger.info("Received message: {}", msg)
      sender < "PONG"
      finish()
      logger.info("Job status: {}", status)
  }
}
