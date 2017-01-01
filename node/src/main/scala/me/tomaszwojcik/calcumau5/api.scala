package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.impl.NodeContextImpl

import scala.concurrent.Future

object api {

  trait Node {
    val ctx: NodeContext = new NodeContextImpl
    var sender: NodeRef = _

    def receive: PartialFunction[AnyRef, Unit]

    def beforeStop(): Unit = {}

    def afterStop(): Unit = {}
  }

  trait NodeContext {
    def remoteNode(nodeID: String): NodeRef
  }

  trait NodeRef {
    def tell(msg: AnyRef): Unit

    def ask(msg: AnyRef): Future[AnyRef]
  }

}
