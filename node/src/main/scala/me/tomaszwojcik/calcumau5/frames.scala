package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.types.NodeID

object frames {

  type FrameHandler = Frame => Unit

  sealed abstract class Frame

  case class Message(sender: NodeID, recipient: NodeID, msg: Array[Byte]) extends Frame

  case class Run(nodes: Map[NodeID, String]) extends Frame

  case object Ping extends Frame

  case object Pong extends Frame

  case object Disconnect extends Frame

  case class File(bytes: Array[Byte]) extends Frame

}
