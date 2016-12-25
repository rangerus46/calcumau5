package me.tomaszwojcik.calcumau5.util

import java.time.{Duration => JDuration}

import scala.concurrent.duration.{Duration => SDuration}
import scala.language.implicitConversions

object DurationConversions {

  implicit def toScala(d: JDuration): SDuration = SDuration.fromNanos(d.toNanos)

  implicit def toJava(d: SDuration): JDuration = JDuration.ofNanos(d.toNanos)

}
