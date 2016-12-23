package me.tomaszwojcik.calcumau5

trait Context[OK, OV] {
  def emit(key: OK, value: OV): Unit
}
