package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.api.{Node, NodeRef}
import me.tomaszwojcik.calcumau5.util.Logging

import scala.concurrent.Future

class NodeRefImpl(ctx: NodeContextImpl, val name: String, val clazz: Class[_ <: Node])
  extends NodeRef with Logging {

  override def tell(msg: AnyRef): Unit = ctx.tell(this, msg)

  override def ask(msg: AnyRef): Future[AnyRef] = ctx.ask(this, msg)
}
