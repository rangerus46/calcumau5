package me.tomaszwojcik.calcumau5

object actions {

  type Opts = Map[String, Any]
  type Action = _Action

  sealed abstract class _Action

  case object Help extends Action

  case object Upload extends Action

  case object Start extends Action

}
