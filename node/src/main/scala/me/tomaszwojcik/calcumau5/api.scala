package me.tomaszwojcik.calcumau5

import scala.concurrent.Future

object api {

  trait Node {
    var ctx: NodeContext = new NodeContextImpl
    var sender: NodeRef = _

    def receive: PartialFunction[AnyRef, Unit]

    def beforeStart(): Unit = {}

    def beforeStop(): Unit = {}

    def afterStop(): Unit = {}
  }

  trait NodeContext {
    def newNode[A <: Node](name: String)(implicit mf: Manifest[A]): NodeRef

    def getNode[A <: Node](name: String)(implicit mf: Manifest[A]): NodeRef
  }

  trait NodeRef {
    def tell(msg: AnyRef): Unit

    def ask(msg: AnyRef): Future[AnyRef]
  }

}
