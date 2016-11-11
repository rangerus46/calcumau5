package me.tomaszwojcik.calcumau5.health

import com.softwaremill.macwire.wire

trait HealthModule {
  lazy val healthServlet = wire[HealthServlet]
}
