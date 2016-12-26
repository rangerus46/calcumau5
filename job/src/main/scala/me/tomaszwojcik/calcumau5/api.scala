package me.tomaszwojcik.calcumau5

import scala.concurrent.Future

object api {

  trait Node {
    var ctx: NodeCtx = _
    var sender: NodeRef = _

    def receive: PartialFunction[AnyRef, Unit]

    def beforeStart(): Unit = {}

    def beforeStop(): Unit = {}

    def afterStop(): Unit = {}
  }

  trait NodeCtx {
    def newNode[A <: Node](implicit mf: Manifest[A]): NodeRef

    def existingNode(name: String): NodeRef
  }

  trait NodeRef {
    def tell(msg: AnyRef): Unit

    def ask(msg: AnyRef): Future[AnyRef]
  }

}
