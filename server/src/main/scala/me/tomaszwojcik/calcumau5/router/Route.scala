package me.tomaszwojcik.calcumau5.router

class Route(path: String) {

  import Route._

  private val tokens: Array[String] = splitPathIntoTokens(path)

  def matches(path: String): Boolean = {
    val matchedTokens = splitPathIntoTokens(path)

    matchedTokens.length == tokens.length && (matchedTokens zip tokens).forall { case (matchedToken, token) =>
      isPlaceholder(token) || matchedToken.equals(token)
    }
  }

  def extractVars(path: String): Map[String, String] = {
    splitPathIntoTokens(path).zip(tokens).filter { case (_, token) =>
      isPlaceholder(token)
    }.map { case (matchedToken, token) =>
      (token.stripPrefix(":"), matchedToken)
    }.toMap
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Route]

  override def equals(other: Any): Boolean = other match {
    case that: Route => (that canEqual this) && (tokens sameElements that.tokens)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(tokens)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"Route(/${tokens.mkString("/")})"
}

object Route {

  def apply(path: String): Route = new Route(path)

  private def splitPathIntoTokens(s: String): Array[String] = {
    Option(s).map {
      // Strip forward and trailing slashes, split on other slashes, trim the results and filter out empty tokens.
      _.stripPrefix("/").stripSuffix("/").split("/").map(_.trim).filter(_.nonEmpty)
    }.getOrElse {
      Array.empty[String]
    }
  }

  private def isPlaceholder(token: String): Boolean = token.startsWith(":")
}
