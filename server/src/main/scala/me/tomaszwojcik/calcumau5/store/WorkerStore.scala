package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.Worker

trait WorkerStore
  extends BaseStore[Worker, Long]

class SimpleWorkerStore
  extends WorkerStore
    with SimpleBaseStore[Worker, Long]
    with LongIdGenerator
