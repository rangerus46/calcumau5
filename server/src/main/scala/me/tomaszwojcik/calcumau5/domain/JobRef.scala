package me.tomaszwojcik.calcumau5.domain

import java.io.File
import java.net.URLClassLoader

import me.tomaszwojcik.calcumau5.{Conf, Job}

case class JobRef(
  name: String,
  className: String,
  jarName: String
) {

  def getJar: File = new File(Conf.FS.WorkerJarsDir, jarName)

  def getClazz: Class[_ <: Job] = {
    val url = getJar.toURI.toURL
    val loader = new URLClassLoader(Array(url), getClass.getClassLoader)
    loader.loadClass(className).asSubclass(classOf[Job])
  }

}
