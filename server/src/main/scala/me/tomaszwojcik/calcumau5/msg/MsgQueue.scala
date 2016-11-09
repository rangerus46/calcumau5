package me.tomaszwojcik.calcumau5.msg

import com.twitter.util.{Future, Promise}

import scala.collection.mutable

/**
  * Asynchronous thread-safe FIFO message queue.
  */
class MsgQueue {
  private val messages = new mutable.Queue[AnyRef]
  private val promises = new mutable.Queue[Promise[AnyRef]]

  /**
    * Pushes a message to the end of queue.
    *
    * @param msg message to push
    */
  def push(msg: AnyRef): Unit = {
    if (promises.isEmpty) {
      messages += msg
    } else {
      promises.dequeue().setValue(msg)
    }
  }

  /**
    * Returns the first future message from the queue.
    *
    * If there is already a message in the queue, future can be resolved instantly.
    * Otherwise future is resolved after any message was pushed to the queue.
    *
    * @return future message
    */
  def pull(): Future[AnyRef] = {
    if (messages.isEmpty) {
      val promise = new Promise[AnyRef]
      promises += promise
      promise
    } else {
      Future.value(messages.dequeue())
    }
  }

  /**
    * Alias for [[MsgQueue.push]].
    */
  def +=(msg: AnyRef): Unit = push(msg)
}
