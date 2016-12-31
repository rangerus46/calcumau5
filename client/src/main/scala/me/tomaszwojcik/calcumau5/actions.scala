package me.tomaszwojcik.calcumau5

object actions {

  type Opts = Map[String, Any]

  sealed abstract class Action

  case object Help extends Action

  case object Upload extends Action

  case object Run extends Action

}
