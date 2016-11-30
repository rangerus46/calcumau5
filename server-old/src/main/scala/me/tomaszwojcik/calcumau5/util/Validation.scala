package me.tomaszwojcik.calcumau5.util

import javax.servlet.http.HttpServletRequest

import scala.collection.mutable
import scala.util.Try

object Validation {

  case class Error(name: String, msg: String, value: Any)

  class Params(req: HttpServletRequest) {
    protected val errors = new mutable.MutableList[Error]

    def getInt(name: String): IntValidator = {
      val param = req.getParameter(name)
      if (param == null) {
        new IntValidator(None)(name, errors)
      } else {
        val i = Try(param.toInt)
        if (i.isFailure) {
          errors += Error(name, "Must be an integer", param)
        }
        new IntValidator(i.toOption)(name, errors)
      }
    }

    def hasErrors: Boolean = errors.nonEmpty

    override def toString: String = errors.mkString(", ")
  }

  class IntValidator(val value: Option[Int])(name: String, errors: mutable.MutableList[Error]) {
    def nonNull: IntValidator = {
      if (value.isEmpty) errors += Error(name, "Can't be null", value)
      this
    }

    def gt(i: Int): IntValidator = {
      for (v <- value if v <= i) {
        errors += Error(name, s"Must be greater than $i", v)
      }
      this
    }

    def goe(i: Int): IntValidator = {
      for (v <- value if v < 0) {
        errors += Error(name, s"Must be greater than or equal $i", v)
      }
      this
    }

    def lt(i: Int): IntValidator = {
      for (v <- value if v >= 0) {
        errors += Error(name, s"Must be smaller than $i", v)
      }
      this
    }

    def loe(i: Int): IntValidator = {
      for (v <- value if v > 0) {
        errors += Error(name, s"Must be smaller than or equal $i", v)
      }
      this
    }
  }

}
