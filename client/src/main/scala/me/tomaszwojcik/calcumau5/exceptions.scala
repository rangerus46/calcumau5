package me.tomaszwojcik.calcumau5

object exceptions {

  case class NodeNotFoundException(id: String) extends RuntimeException(s"id = $id")

}
