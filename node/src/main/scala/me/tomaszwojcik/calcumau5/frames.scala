package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.types.NodeID

object frames {

  type FrameHandler = Frame => Unit

  sealed abstract class Frame

  case class MsgFrame(sender: NodeID, recipient: NodeID, msg: Array[Byte]) extends Frame

  case class RunFrame(nodes: Map[NodeID, String]) extends Frame

  case object PingFrame extends Frame

  case object PongFrame extends Frame

  case class FileFrame(bytes: Array[Byte]) extends Frame

  case class LogFrame(s: String, from: NodeID) extends Frame

}
