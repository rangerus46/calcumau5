import sbt._

object Dependencies {
  // Versions
  val macwireVersion = "2.2.5"

  val configVersion = "1.3.1"

  val slf4jApiVersion = "1.7.21"
  val logbackVersion = "1.1.7"

  val finagleVersion = "6.39.0"

  // Libraries
  val macwire = "com.softwaremill.macwire" %% "macros" % macwireVersion

  val config = "com.typesafe" % "config" % configVersion

  val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jApiVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion

  val finagleCore = "com.twitter" %% "finagle-core" % finagleVersion
  val finagleHttp = "com.twitter" %% "finagle-http" % finagleVersion

  // Projects
  val jobDeps = Seq(slf4jApi)
  val serverDeps = Seq(macwire, config, slf4jApi, logback, finagleCore, finagleHttp)
}
