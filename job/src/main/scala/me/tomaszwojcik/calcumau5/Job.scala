package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.Job.Args

abstract class Job(args: Args) {
  private var _reader: Class[_ <: Reader[_, _]] = _

  protected def reader_=(c: Class[_ <: Reader[_, _]]): Unit = _reader = c

  def reader: Class[_ <: Reader[_, _]] = _reader


  private var _mapper: Class[_ <: Mapper[_, _, _, _]] = _

  protected def mapper_=(c: Class[_ <: Mapper[_, _, _, _]]): Unit = _mapper = c

  def mapper: Class[_ <: Mapper[_, _, _, _]] = _mapper


  private var _reducer: Class[_ <: Reducer[_, _, _, _]] = _

  protected def reducer_=(c: Class[_ <: Reducer[_, _, _, _]]): Unit = _reducer = c

  def reducer: Class[_ <: Reducer[_, _, _, _]] = _reducer


  private var _writer: Class[_ <: Writer[_, _]] = _

  protected def writer_=(c: Class[_ <: Writer[_, _]]): Unit = _writer = c

  def writer: Class[_ <: Writer[_, _]] = _writer
}

object Job {
  type Args = Map[String, Any]
}
