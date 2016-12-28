package me.tomaszwojcik.calcumau5.test

import me.tomaszwojcik.calcumau5.api._
import me.tomaszwojcik.calcumau5.util.Logging

class PingNode extends Node with Logging {
  log.info("Created new PingNode")

  val pongNode = ctx.getRemote("n0", "s0")
  pongNode.tell("PING")

  override def receive = {
    case msg => log.info("Received a '{}' message", msg)
  }
}
