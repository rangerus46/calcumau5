import sbt._

object Dependencies {
  // Versions
  val macwireVersion = "2.2.5"

  val configVersion = "1.3.1"

  val slf4jApiVersion = "1.7.21"
  val logbackVersion = "1.1.7"

  val jettyVersion = "9.3.14.v20161028"

  val json4sNativeVersion = "3.4.2"

  // Libraries
  val macwire = "com.softwaremill.macwire" %% "macros" % macwireVersion

  val config = "com.typesafe" % "config" % configVersion

  val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jApiVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion % "optional"
  val loggingDeps = Seq(slf4jApi, logback)

  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyClient = "org.eclipse.jetty" % "jetty-client" % jettyVersion
  val jettyDeps = Seq(jettyServer, jettyServlet, jettyClient)

  val json4sNative = "org.json4s" %% "json4s-native" % json4sNativeVersion

  // Projects
  val jobDeps = loggingDeps
  val serverDeps = Seq(macwire, config, json4sNative) ++ loggingDeps ++ jettyDeps
}
