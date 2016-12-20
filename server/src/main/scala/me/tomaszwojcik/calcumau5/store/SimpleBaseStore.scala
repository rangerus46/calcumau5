package me.tomaszwojcik.calcumau5.store

import scala.collection.mutable

trait BaseStore[A, I] {
  def add(a: A): Unit

  def remove(iD: I): Unit

  def retrieveAll(): List[A]

  def findById(id: I): Option[A]
}

trait SimpleBaseStore[A, I] extends BaseStore[A, I] {
  this: IdGenerator[I] =>

  private val instances = new mutable.HashMap[I, A]

  override def add(a: A): Unit = synchronized {
    val id = nextId()
    instances.put(id, a)
  }

  override def remove(id: I): Unit = synchronized {
    instances.remove(id)
  }

  override def retrieveAll(): List[A] = synchronized {
    instances.values.toList
  }

  override def findById(id: I): Option[A] = synchronized {
    instances.get(id)
  }
}

trait IdGenerator[A] {
  def nextId(): A
}

trait LongIdGenerator extends IdGenerator[Long] {
  private var lastId = 0L

  override def nextId(): Long = {
    lastId += 1
    lastId
  }
}
