package me.tomaszwojcik.calcumau5.test

import me.tomaszwojcik.calcumau5.api._
import me.tomaszwojcik.calcumau5.util.Logging

class PingNode extends Node with Logging {
  log.info("Created new PingNode")

  val pongNode = ctx.remoteNode("pong-node")

  log.info("Sent message: PING")
  pongNode.!("PING")

  override def receive = {
    case "PONG" => log.info("Received message: PONG")
  }
}
