package me.tomaszwojcik.calcumau5.http

import com.softwaremill.macwire.wire
import com.twitter.finagle.http.Method.{Get, Post}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import me.tomaszwojcik.calcumau5.msg.MsgModule
import me.tomaszwojcik.calcumau5.worker.WorkerModule

trait ServicesModule {
  this: MsgModule with WorkerModule =>

  private lazy val workerServices: WorkerServices = wire[WorkerServices]
  private lazy val jobServices: JobServices = wire[JobServices]
  private lazy val messageServices: MsgServices = wire[MsgServices]

  lazy val routingService = RoutingService.byMethodAndPathObject {
    case Post -> Root / "workers" => workerServices.register()

    case Get -> Root / "jobs" => jobServices.list()
    case Post -> Root / "jobs" => jobServices.receive()

    case Post -> Root / "messages" => messageServices.receive()
  }
}
