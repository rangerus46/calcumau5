package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.ArgsParser.Opts

import scala.annotation.tailrec

class ArgsParser(args: List[String]) {

  lazy val result: (Symbol, Opts) = args match {
    case "deploy" :: tail => ('deploy, parseOpts(tail))
    case "run" :: tail => ('run, parseOpts(tail))
    case _ => ('help, Map.empty)
  }

  @tailrec private def parseOpts(args: List[String], opts: Opts = Map.empty): Opts = args match {
    case "--config" :: path :: tail => parseOpts(tail, opts + ('config -> path))
    case "--jar" :: path :: tail => parseOpts(tail, opts + ('jar -> path))
    case Nil => opts
    case _ :: tail => parseOpts(tail, opts)
  }

}

object ArgsParser {
  type Opts = Map[Symbol, String]
}
