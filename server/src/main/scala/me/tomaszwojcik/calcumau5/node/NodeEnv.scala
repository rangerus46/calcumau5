package me.tomaszwojcik.calcumau5.node

import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.events.inbound.{InEvt, InMsg}
import me.tomaszwojcik.calcumau5.events.outbound.{OutEvtHandler, OutMsg, SelfMsg}
import me.tomaszwojcik.calcumau5.frames.FrameHandler
import me.tomaszwojcik.calcumau5.types.NodeID
import me.tomaszwojcik.calcumau5.{Serializers, frames}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class NodeEnv(
  classLoader: ClassLoader,
  frameHandler: FrameHandler,
  nodeDefs: Map[NodeID, String] = Map.empty) {

  private val executionsByNodeID = new mutable.HashMap[NodeID, NodeExec]

  for ((id, className) <- nodeDefs) {
    val c = classLoader.loadClass(className).asSubclass(classOf[Node])
    val handler = outEvtHandlerForNode(id)
    val exec = new NodeExec(id, c, handler)
    executionsByNodeID.put(id, exec)
  }

  def pushEvent: PartialFunction[InEvt, Unit] = {
    case evt: InMsg => executionsByNodeID(evt.to).pushEvent(evt)
  }

  def start()(implicit ec: ExecutionContext): Unit = {
    for (exec <- executionsByNodeID.values) {
      ec.execute(exec)
    }
  }

  def outEvtHandlerForNode(nodeID: NodeID): OutEvtHandler = {
    case outMsg: OutMsg =>
      val bytes = Serializers.serializeMsg(outMsg.msg)
      val frame = frames.Message(nodeID, outMsg.to, bytes)
      frameHandler.apply(frame)

    // Loopback for self messages.
    case selfMsg: SelfMsg =>
      val evt = InMsg(selfMsg.msg, nodeID, nodeID)
      pushEvent(evt)
  }

}
