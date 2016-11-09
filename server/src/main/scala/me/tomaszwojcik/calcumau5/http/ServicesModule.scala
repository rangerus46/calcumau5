package me.tomaszwojcik.calcumau5.http

import com.softwaremill.macwire.wire
import com.twitter.finagle.http.Method.{Get, Post}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService

trait ServicesModule {
  private lazy val jobServices = wire[JobServices]
  private lazy val messageServices = wire[MessageServices]

  val routingService = RoutingService.byMethodAndPathObject {
    case Get -> Root / "jobs" => jobServices.list()
    case Post -> Root / "jobs" => jobServices.receive()

    case Post -> Root / "messages" => messageServices.receive()
  }
}
