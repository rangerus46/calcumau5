package me.tomaszwojcik.calcumau5.worker

import com.softwaremill.macwire.wire

trait WorkerModule {
  implicit lazy val workerStore = wire[WorkerStore]
}
