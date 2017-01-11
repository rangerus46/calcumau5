package me.tomaszwojcik.calcumau5.node

import java.util.concurrent.ConcurrentHashMap

import me.tomaszwojcik.calcumau5.Serializers
import me.tomaszwojcik.calcumau5.api.Node
import me.tomaszwojcik.calcumau5.events.DieEvt
import me.tomaszwojcik.calcumau5.events.inbound.{InEvt, InMsgEvt}
import me.tomaszwojcik.calcumau5.events.outbound._
import me.tomaszwojcik.calcumau5.frames.{ErrorFrame, FrameHandler, LogFrame, MsgFrame}
import me.tomaszwojcik.calcumau5.types.NodeID

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext

class NodeEnv(
  classLoader: ClassLoader,
  frameHandler: FrameHandler,
  nodeDefs: Map[NodeID, String] = Map.empty) {

  private val execsByNodeID = new ConcurrentHashMap[NodeID, NodeExec]

  for ((id, className) <- nodeDefs) {
    val c = classLoader.loadClass(className).asSubclass(classOf[Node])
    val handler = outEvtHandlerForNode(id)
    val exec = new NodeExec(id, c, handler)
    execsByNodeID.put(id, exec)
  }

  def pushEvent: PartialFunction[InEvt, Unit] = {
    case evt: InMsgEvt => execsByNodeID.get(evt.to).pushEvent(evt)
  }

  def start()(implicit ec: ExecutionContext): Unit = {
    for (exec <- execsByNodeID.values()) {
      ec.execute(exec)
    }
  }

  def outEvtHandlerForNode(nodeID: NodeID): OutEvtHandler = {
    case OutMsgEvt(msg, to) =>
      val bytes = Serializers.serializeMsg(msg)
      val frame = MsgFrame(sender = nodeID, to, bytes)
      frameHandler.apply(frame)

    // Loopback for self messages.
    case SelfMsgEvt(msg) =>
      val evt = InMsgEvt(msg, nodeID, nodeID)
      pushEvent(evt)

    case DieEvt =>
      val exec = execsByNodeID.remove(nodeID)
      exec.pushEvent(DieEvt)

    case LogEvt(s) =>
      val frame = LogFrame(s, nodeID)
      frameHandler.apply(frame)

    case ErrorEvt(e) =>
      val frame = ErrorFrame(Some(nodeID), e)
      frameHandler.apply(frame)

  }

}
