package me.tomaszwojcik.calcumau5.store

import scala.collection.mutable

trait BaseStore[A] {
  def add(a: A): Unit

  def remove(a: A): Unit

  def retrieveAll(): List[A]
}

trait SimpleBaseStore[A] extends BaseStore[A] {
  private val instances = new mutable.ArrayBuffer[A]

  override def add(a: A): Unit = {
    instances += a
  }

  override def remove(a: A): Unit = {
    instances -= a
  }

  override def retrieveAll(): List[A] = {
    instances.toList
  }
}
