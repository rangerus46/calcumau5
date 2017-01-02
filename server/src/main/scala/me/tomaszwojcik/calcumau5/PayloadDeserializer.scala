package me.tomaszwojcik.calcumau5

import java.io.{ByteArrayInputStream, InputStream, ObjectInputStream, ObjectStreamClass}

class PayloadDeserializer(loader: ClassLoader, frame: frames.Message) {

  private lazy val is = new ObjectInputStreamWithClassLoader(loader, new ByteArrayInputStream(frame.payload))

  private lazy val payload = {
    try {
      is.readObject()
    } finally {
      is.close()
    }
  }

  def get: AnyRef = payload

  private class ObjectInputStreamWithClassLoader(loader: ClassLoader, is: InputStream)
    extends ObjectInputStream(is) {

    override def resolveClass(desc: ObjectStreamClass): Class[_] = Class.forName(desc.getName, false, loader)

  }

}
