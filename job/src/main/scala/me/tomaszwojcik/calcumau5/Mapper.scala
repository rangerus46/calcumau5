package me.tomaszwojcik.calcumau5

trait Mapper[IK, IV, OK, OV] {
  def map(key: IK, value: IV, ctx: Context[OK, OV]): Unit
}
