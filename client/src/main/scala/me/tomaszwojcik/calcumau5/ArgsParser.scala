package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.actions._

class ArgsParser(args: List[String]) {

  lazy val result: (Action, Opts) = args match {
    case action :: tail => (parseAction(action), parseOpts(tail))
    case _ => (actions.Help, Map.empty)
  }

  private def parseAction(arg: String): Action = arg match {
    case "upload" => actions.Upload
    case "start" => actions.Start
    case _ => actions.Help
  }

  private def parseOpts(args: List[String], opts: Opts = Map.empty): Opts = args match {
    case "--config" :: path :: tail => parseOpts(tail, opts + ("config" -> path))
    case Nil => opts
    case _ :: tail => parseOpts(tail, opts)
  }

}
