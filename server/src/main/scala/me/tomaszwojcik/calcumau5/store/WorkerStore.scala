package me.tomaszwojcik.calcumau5.store

import me.tomaszwojcik.calcumau5.domain.Worker
import me.tomaszwojcik.calcumau5.util.Logging

import scala.collection.mutable

trait WorkerStore extends BaseStore[Worker]

class SimpleWorkerStore extends WorkerStore with SimpleBaseStore[Worker]
