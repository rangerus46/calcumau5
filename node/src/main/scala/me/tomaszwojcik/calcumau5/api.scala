package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.impl.ContextImpl

object api {

  trait Node {
    private val contextImpl = new ContextImpl

    def ctx: Context = contextImpl

    def sender: NodeRef = contextImpl.sender

    def self: NodeRef = contextImpl.self

    def receive: PartialFunction[AnyRef, Unit]

    def beforeStop(): Unit = {}

    def afterStop(): Unit = {}
  }

  trait Context {
    def remoteNode(nodeID: String): NodeRef

    def die(): Unit
  }

  trait NodeRef {
    def !(msg: AnyRef): Unit
  }

}
