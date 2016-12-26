package me.tomaszwojcik.calcumau5.test

import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging

class PongNode extends Node with Logging {
  log.info("Created new PongNode")

  override def receive = {
    case "PING" =>
      log.info("Received 'PING' message")
      sender.tell("PONG")
  }
}
