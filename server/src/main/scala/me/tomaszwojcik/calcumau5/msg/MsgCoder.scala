package me.tomaszwojcik.calcumau5.msg

import java.io._

import scala.io.Source
import scala.util.Try

// TODO: implement properly
class MsgCoder {
  def encode(os: OutputStream, msg: AnyRef): Unit = {
    os.write(msg.toString.getBytes)
  }

  def decode(is: InputStream): Try[AnyRef] = Try {
    Source.fromInputStream(is).mkString
  }
}
