package me.tomaszwojcik.calcumau5

import java.util.concurrent.ScheduledThreadPoolExecutor

import org.json4s.DefaultFormats

import scala.concurrent.ExecutionContext

object Implicits {
  // Json4s
  implicit lazy val formats = DefaultFormats

  // Concurrency
  implicit lazy val scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4)
  implicit lazy val executionContext = ExecutionContext.fromExecutor(scheduledThreadPoolExecutor)
}
