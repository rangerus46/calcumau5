package me.tomaszwojcik.calcumau5

import java.io._

object Serializers {

  private class CustomObjectInputStream(loader: ClassLoader, is: InputStream)
    extends ObjectInputStream(is) {

    override def resolveClass(desc: ObjectStreamClass): Class[_] = {
      Class.forName(desc.getName, false, loader)
    }

  }

  def serializeMsg(msg: AnyRef): Array[Byte] = {
    var os0: ByteArrayOutputStream = null
    var os1: ObjectOutputStream = null
    try {
      os0 = new ByteArrayOutputStream()
      os1 = new ObjectOutputStream(os0)
      os1.writeObject(msg)
      os0.toByteArray
    } finally {
      if (os0 != null) os0.close()
      if (os1 != null) os1.close()
    }
  }

  def deserializeMsg(bytes: Array[Byte], classLoader: ClassLoader): AnyRef = {
    var is: ObjectInputStream = null
    try {
      is = new CustomObjectInputStream(classLoader, new ByteArrayInputStream(bytes))
      is.readObject()
    } finally {
      if (is != null) is.close()
    }
  }

}
