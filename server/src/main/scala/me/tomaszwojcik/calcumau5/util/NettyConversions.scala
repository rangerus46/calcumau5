package me.tomaszwojcik.calcumau5.util

import io.netty.util.concurrent.{Future, FutureListener}

import scala.language.implicitConversions

object NettyConversions {
  implicit def fn2FutureListener[A](fn: Future[A] => Unit): FutureListener[A] = new FutureListener[A] {
    override def operationComplete(f: Future[A]): Unit = fn(f)
  }
}
