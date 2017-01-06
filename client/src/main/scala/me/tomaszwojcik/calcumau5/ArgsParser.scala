package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.ArgsParser.Opts

class ArgsParser(args: List[String]) {

  lazy val result: (Symbol, Opts) = args match {
    case "deploy" :: tail => ('deploy, parseOpts(tail))
    case "run" :: tail => ('run, parseOpts(tail))
    case _ => ('help, Map.empty)
  }

  private def parseOpts(args: List[String], opts: Opts = Map.empty): Opts = args match {
    case "--config" :: path :: tail => parseOpts(tail, opts + ("config" -> path))
    case "--file" :: path :: tail => parseOpts(tail, opts + ("file" -> path))
    case Nil => opts
    case _ :: tail => parseOpts(tail, opts)
  }

}

object ArgsParser {
  type Opts = Map[String, Any]
}
