package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.actions._

class ArgsParser(args: List[String]) {

  lazy val result: (Action, Opts) = args match {
    case "deploy" :: tail => (actions.Deploy, parseOpts(tail))
    case "run" :: tail => (actions.Run, parseOpts(tail))
    case _ => (actions.Help, Map.empty)
  }

  private def parseOpts(args: List[String], opts: Opts = Map.empty): Opts = args match {
    case "--config" :: path :: tail => parseOpts(tail, opts + ("config" -> path))
    case "--file" :: path :: tail => parseOpts(tail, opts + ("file" -> path))
    case Nil => opts
    case _ :: tail => parseOpts(tail, opts)
  }

}
