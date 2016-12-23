package me.tomaszwojcik.calcumau5

trait Reducer[IK, IV, OK, OV] {
  def reduce(key: IK, values: Seq[IV], ctx: Context[OK, OV]): Unit
}
