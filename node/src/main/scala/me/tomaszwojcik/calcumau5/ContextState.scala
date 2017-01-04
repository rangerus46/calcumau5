package me.tomaszwojcik.calcumau5

import scala.collection.mutable

trait ContextState {
  def sendToRemote(msg: AnyRef, to: String): Unit

  def sendToSelf(msg: AnyRef): Unit
}

class InitialContextState extends ContextState {

  import InitialContextState._

  private val calls = new mutable.Queue[DeferredCall]

  override def sendToRemote(msg: AnyRef, to: String): Unit = calls += SendToRemote(msg, to)

  override def sendToSelf(msg: AnyRef): Unit = calls += SendToSelf(msg)

  def migrateTo(state: ContextState): Unit = calls.foreach {
    case SendToRemote(msg, to) => state.sendToRemote(msg, to)
    case SendToSelf(msg) => state.sendToSelf(msg)
  }

}

object InitialContextState {

  private sealed abstract class DeferredCall

  private case class SendToRemote(msg: AnyRef, to: String) extends DeferredCall

  private case class SendToSelf(msg: AnyRef) extends DeferredCall

}


