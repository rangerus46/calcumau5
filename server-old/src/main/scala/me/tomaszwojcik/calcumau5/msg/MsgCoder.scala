package me.tomaszwojcik.calcumau5.msg

import java.io._

import scala.util.Try

class MsgCoder {
  def encode(os: OutputStream, msg: AnyRef): Unit = {
    new ObjectOutputStream(os).writeObject(msg)
  }

  def decode(is: InputStream): Try[AnyRef] = Try {
    new ObjectInputStream(is).readObject()
  }
}
