import Dependencies._

lazy val commonSettings = Seq(
  organization := "me.tomaszwojcik",
  scalaVersion := "2.11.8",
  version := "0.1.0"
)

lazy val commons = (project in file("commons")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-commons",
    libraryDependencies ++= Seq(slf4jApi),
    libraryDependencies ++= nettyModules
  )

lazy val server = (project in file("server")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-server",
    libraryDependencies ++= Seq(
      scalaTest,
      macwire,
      typesafeConfig,
      json4sNative,
      slf4jApi,
      logback
    ),
    libraryDependencies ++= nettyModules
  ).
  dependsOn(commons, node)

lazy val client = (project in file("client")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-client",
    libraryDependencies ++= Seq(
      scalaTest,
      macwire,
      typesafeConfig,
      json4sNative,
      slf4jApi,
      logback
    ),
    libraryDependencies ++= nettyModules
  ).
  dependsOn(commons, node)

lazy val node = (project in file("node")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-node",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      scalaTest,
      slf4jApi,
      logback
    )
  ).
  dependsOn(commons)

lazy val exampleNode = (project in file("example-node")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-example-node",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      slf4jApi,
      logback
    )
  ).
  dependsOn(node)
