package me.tomaszwojcik.calcumau5

object events {

  type EventHandler = Event => Unit

  sealed abstract class Event

  abstract class Message(payload: AnyRef, to: String, from: String) extends Event

  case class InboundMessage(payload: AnyRef, to: String, from: String) extends Message(payload, to, from)

  case class OutboundMessage(payload: AnyRef, to: String, from: String) extends Message(payload, to, from)

}
