import Dependencies._
import xerial.sbt.Pack._

lazy val commonSettings = Seq(
  organization := "me.tomaszwojcik.calcumau5",
  scalaVersion := "2.11.8"
)

lazy val server = (project in file("server")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-server",
    version := "0.1.0",
    libraryDependencies ++= serverDeps
  ).
  settings(packAutoSettings: _*)

lazy val job = (project in file("job")).
  settings(commonSettings: _*).
  settings(
    name := "calcumau5-job",
    version := "0.1.0",
    libraryDependencies ++= jobDeps
  )

