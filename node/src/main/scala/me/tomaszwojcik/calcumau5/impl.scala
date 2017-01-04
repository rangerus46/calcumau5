package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.{Context, NodeRef}

import scala.concurrent.Future

object impl {

  class ContextImpl extends Context {

    private var state: ContextState = new InitialContextState

    // Refs

    lazy val self = new NodeRef {
      override def !(msg: AnyRef): Unit = state.sendToSelf(msg)

      override def ?(msg: AnyRef): Future[AnyRef] = ???
    }

    override def remoteNode(nodeID: String): NodeRef = new NodeRef {
      override def !(msg: AnyRef): Unit = state.sendToRemote(msg, to = nodeID)

      override def ?(msg: AnyRef): Future[AnyRef] = ???
    }

    def sender: NodeRef = ???

    // Operations

    override def die(): Unit = ???

    def init(state: ContextState): Unit = this.state match {
      case s: InitialContextState =>
        s.migrateTo(state)
        this.state = state
      case _ => throw new UnsupportedOperationException("Already initialized")
    }

  }

}
