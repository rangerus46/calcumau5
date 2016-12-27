package me.tomaszwojcik.calcumau5.test

import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.util.Logging

class PingNode extends Node with Logging {
  log.info("Created new PingNode")

  val pongNode = ctx.newNode[PongNode](name = "pong-node")
  pongNode.tell("PING")

  override def receive = {
    case msg => log.info("Received a '{}' message", msg)
  }
}
