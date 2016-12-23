package me.tomaszwojcik.calcumau5

import java.io.Closeable

import me.tomaszwojcik.calcumau5.Job.Args

abstract class Reader[OK, OV](args: Args) extends Closeable {
  def readAll(ctx: Context[OK, OV]): Unit
}
