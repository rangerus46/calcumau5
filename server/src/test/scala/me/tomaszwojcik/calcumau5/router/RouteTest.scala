package me.tomaszwojcik.calcumau5.router

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class RouteTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  "A Route with '/' path" should "be equal to a Route with null path" in {
    Route("/") should equal(Route(null))
  }

  it should "be equal to a Route with empty path" in {
    Route("/") should equal(Route(""))
  }

  it should "be equal to a Route with blank path" in {
    Route("/") should equal(Route(" "))
  }

  val constPaths = Table(
    ("route", "path", "result"),

    (Route("/"), "/", true),
    (Route("/"), null, true),
    (Route("/"), " ", true),
    (Route("/"), "", true),
    (Route("/"), "a", false),

    (Route("/a"), "/a", true),
    (Route("/a"), "/a//", true),
    (Route("/a"), "/a/", true),
    (Route("/a"), "a/", true),
    (Route("/a"), "a", true),
    (Route("/a"), "b", false),

    (Route("/a"), "/a/b", false),
    (Route("/a"), "/a/b/", false),
    (Route("/a"), "/a/b/c", false),
    (Route("/a"), "/a/b/c/", false),

    (Route("/a/b"), "/a", false),
    (Route("/a/b/"), "/a", false),
    (Route("/a/b/c"), "/a", false),
    (Route("/a/b/c/"), "/a", false)
  )

  forAll(constPaths) { (route: Route, path: String, res: Boolean) =>
    route.matches(path) shouldEqual res
  }

  val varRoutes = Table(
    ("route", "path", "result"),

    (Route("/items/:id"), "/items", false),
    (Route("/items/:id"), "/items/ /", false),
    (Route("/items/:id"), "/items//", false),
    (Route("/items/:id"), "/items/1", true),
    (Route("/items/:id"), "/items/1/", true),
    (Route("/items/:id/sub-items"), "/items/1/sub-items", true),
    (Route("/items/:id/sub-items"), "/items//sub-items", false),
    (Route("/items/:id/sub-items"), "/items/sub-items", false)
  )

  forAll(varRoutes) { (route: Route, path: String, res: Boolean) =>
    route.matches(path) shouldEqual res
  }

  "A Route with one variable" should "extract variable from path" in {
    val vars = Route("/items/:id").extractVars("/items/1")
    assertResult(1)(vars.size)
    assertResult(Some("1"))(vars get "id")
  }

  "A Route with two variables" should "extract variables from path" in {
    val vars = Route("/items/:id/sub-items/:sub-id").extractVars("/items/1/sub-items/2")
    assertResult(2)(vars.size)
    assertResult(Some("1"))(vars get "id")
    assertResult(Some("2"))(vars get "sub-id")
  }

}
