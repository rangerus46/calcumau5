package me.tomaszwojcik.calcumau5.router

import io.netty.channel.{ChannelHandlerContext => CHCtx}
import io.netty.handler.codec.http.{FullHttpRequest, HttpMethod}

class Route(
  val method: HttpMethod,
  val matcher: String,
  val handler: (CHCtx, FullHttpRequest, Map[String, String]) => Unit) {

  import Route._

  private val tokens: Array[String] = splitMatcherIntoTokens(matcher)

  def matches(method: HttpMethod, path: String): Boolean = {
    if (this.method != method) {
      false
    } else {
      val matchedTokens = splitMatcherIntoTokens(path)
      matchedTokens.length == tokens.length && (matchedTokens zip tokens).forall { case (matchedToken, token) =>
        isPlaceholder(token) || matchedToken.equals(token)
      }
    }
  }

  def extractVars(path: String): Map[String, String] = {
    splitMatcherIntoTokens(path).zip(tokens).filter { case (_, token) =>
      isPlaceholder(token)
    }.map { case (matchedToken, token) =>
      (token.stripPrefix(":"), matchedToken)
    }.toMap
  }

  override def toString = s"Route(${method.name} /${tokens.mkString("/")})"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Route]

  override def equals(other: Any): Boolean = other match {
    case that: Route =>
      (that canEqual this) &&
        (tokens sameElements that.tokens) &&
        method == that.method
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(tokens, method)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Route {

  def apply(method: HttpMethod, matcher: String, fn: (CHCtx, FullHttpRequest, Map[String, String]) => Unit): Route = {
    new Route(method, matcher, fn)
  }

  def splitMatcherIntoTokens(s: String): Array[String] = {
    Option(s).map {
      // Strip forward and trailing slashes, split on other slashes, trim the results and filter out empty tokens.
      _.stripPrefix("/").stripSuffix("/").split("/").map(_.trim).filter(_.nonEmpty)
    }.getOrElse {
      Array.empty[String]
    }
  }

  private def isPlaceholder(token: String): Boolean = token.startsWith(":")
}
