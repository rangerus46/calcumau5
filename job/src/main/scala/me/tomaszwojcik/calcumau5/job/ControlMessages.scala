package me.tomaszwojcik.calcumau5.job

object ControlMessages {

  sealed abstract class Msg

  case object Begin extends Msg

}
