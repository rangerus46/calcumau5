package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.{Node, NodeContext, NodeRef}
import me.tomaszwojcik.calcumau5.frames.{Frame, GetNodeFrame, NewNodeFrame, TellFrame}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable
import scala.concurrent.Future

class NodeContextImpl extends NodeContext with Logging {

  private var frameHandler: Option[(Frame) => Unit] = None
  private val frameQueue = new mutable.Queue[Frame]

  override def newNode[A <: Node](name: String)(implicit mf: Manifest[A]): NodeRef = synchronized {
    val ref = createRef(name)
    handleFrame(NewNodeFrame(ref))
    ref
  }

  override def getNode[A <: Node](name: String)(implicit mf: Manifest[A]): NodeRef = synchronized {
    val ref = createRef(name)
    handleFrame(GetNodeFrame(ref))
    ref
  }

  def tell(ref: NodeRef, msg: AnyRef): Unit = {
    handleFrame(TellFrame(ref, msg))
  }

  def ask(ref: NodeRef, msg: AnyRef): Future[AnyRef] = ???

  private def createRef(name: String)(implicit mf: Manifest[_ <: Node]): NodeRef = {
    val c = mf.runtimeClass.asSubclass(classOf[Node])
    new NodeRefImpl(this, name, c)
  }

  private def handleFrame(frame: Frame): Unit = {
    frameHandler match {
      case Some(fn) => fn(frame)
      case None => frameQueue += frame
    }
  }

  def setFrameHandler(fn: (Frame) => Unit): Unit = synchronized {
    frameHandler = Option(fn)
    if (frameHandler.isDefined) {
      while (frameQueue.nonEmpty) {
        val frame = frameQueue.dequeue()
        fn(frame)
      }
    }
  }
}
