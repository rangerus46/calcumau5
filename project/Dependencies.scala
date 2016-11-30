import sbt._

object Dependencies {
  // Versions
  val macwireVersion = "2.2.5"

  val configVersion = "1.3.1"

  val slf4jApiVersion = "1.7.21"
  val logbackVersion = "1.1.7"

  val nettyVersion = "4.1.6.Final"
  val nettyRouterVersion = "2.0.0"

  val json4sNativeVersion = "3.4.2"

  // Libraries
  val macwire: ModuleID = "com.softwaremill.macwire" %% "macros" % macwireVersion

  val config: ModuleID = "com.typesafe" % "config" % configVersion

  val slf4jApi: ModuleID = "org.slf4j" % "slf4j-api" % slf4jApiVersion
  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % logbackVersion % "optional"
  val loggingModules = Seq(slf4jApi, logback)

  val nettyModules: Seq[ModuleID] = Seq(
    "io.netty" % "netty-all" % nettyVersion,
    "tv.cntt" % "netty-router" % nettyRouterVersion
  )

  val json4sNative: ModuleID = "org.json4s" %% "json4s-native" % json4sNativeVersion

  // Projects
  val jobDeps: Seq[ModuleID] = loggingModules
  val serverDeps: Seq[ModuleID] = Seq(macwire, config, json4sNative) ++ loggingModules ++ nettyModules
}
