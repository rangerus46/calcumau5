package me.tomaszwojcik.calcumau5.service

import java.io.File
import java.net.URLClassLoader

import me.tomaszwojcik.calcumau5.Entities.JobRef
import me.tomaszwojcik.calcumau5.Job.Args
import me.tomaszwojcik.calcumau5._
import me.tomaszwojcik.calcumau5.domain.JobExec
import me.tomaszwojcik.calcumau5.util.Logging

import scala.util.Try

class JobExecutorService extends Logging {

  type AnyContext = Context[Any, Any]
  type AnyReader = Reader[Any, Any]
  type AnyMapper = Mapper[Any, Any, Any, Any]
  type AnyReducer = Reducer[Any, Any, Any, Any]
  type AnyWriter = Writer[Any, Any]

  def startJob(ref: JobRef, args: Args): Unit = synchronized {
    val jar = new File(Conf.FS.WorkerJarsDir, ref.jarName)
    val url = jar.toURI.toURL
    val loader = new URLClassLoader(Array(url), getClass.getClassLoader)
    val clazz = loader.loadClass(ref.className).asSubclass(classOf[Job])

    val job = createWithArgs(clazz, args)
    val exec = JobExec(job, args)

    val reader = createWithArgs(exec.job.reader, exec.args).asInstanceOf[AnyReader]
    val mapper = createWithArgs(exec.job.mapper, exec.args).asInstanceOf[AnyMapper]
    val reducer = createWithArgs(exec.job.reducer, exec.args).asInstanceOf[AnyReducer]
    val writer = createWithArgs(exec.job.writer, exec.args).asInstanceOf[AnyWriter]

    reader.readAll(readerCtx)
    reader.close()
    writer.close()

    lazy val readerCtx = new AnyContext {
      override def emit(key: Any, value: Any): Unit = mapper.map(key, value, mapperCtx)
    }

    lazy val mapperCtx = new AnyContext {
      override def emit(key: Any, value: Any): Unit = reducer.reduce(key, Seq(value), reducerCtx)
    }

    lazy val reducerCtx = new AnyContext {
      override def emit(key: Any, value: Any): Unit = writer.write(key, value)
    }
  }

  private def createWithArgs[A](
    cls: Class[A],
    args: Args
  ): A = {
    Try {
      val c = cls.getConstructor(classOf[Args])
      c.newInstance(args)
    } getOrElse {
      val c = cls.getConstructor()
      c.newInstance()
    }
  }

}
