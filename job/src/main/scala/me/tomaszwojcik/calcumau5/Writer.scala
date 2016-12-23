package me.tomaszwojcik.calcumau5

import java.io.Closeable

trait Writer[IK, IV] extends Closeable {
  def begin(): Unit = {}

  def write(key: IK, value: IV): Unit

  override def close(): Unit = {}
}
