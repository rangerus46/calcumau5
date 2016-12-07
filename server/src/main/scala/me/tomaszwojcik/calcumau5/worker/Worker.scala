package me.tomaszwojcik.calcumau5.worker

class Worker(val host: String, val port: Int) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[Worker]

  override def equals(other: Any): Boolean = other match {
    case that: Worker =>
      (that canEqual this) &&
        host == that.host &&
        port == that.port
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(host, port)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"Worker(host=$host, port=$port)"

  def unapply(arg: Worker): Option[(String, Int)] = Option((arg.host, arg.port))
}

object Worker {
  def apply(host: String, port: Int): Worker = new Worker(host, port)
}
