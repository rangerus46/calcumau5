import sbt._

object Dependencies {
  // Versions
  val scalaTestVersion = "3.0.1"

  val macwireVersion = "2.2.5"

  val configVersion = "1.3.1"

  val slf4jApiVersion = "1.7.21"
  val logbackVersion = "1.1.7"

  val nettyVersion = "4.1.6.Final"

  val json4sNativeVersion = "3.4.2"

  // Libraries
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

  val macwire: ModuleID = "com.softwaremill.macwire" %% "macros" % macwireVersion

  val typesafeConfig: ModuleID = "com.typesafe" % "config" % configVersion

  val slf4jApi: ModuleID = "org.slf4j" % "slf4j-api" % slf4jApiVersion
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % logbackVersion % "optional"

  val nettyModules: Seq[ModuleID] = Seq(
    "io.netty" % "netty-all" % nettyVersion
  )

  val json4sNative: ModuleID = "org.json4s" %% "json4s-native" % json4sNativeVersion
}
