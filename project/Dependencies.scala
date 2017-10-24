import sbt._

object Dependencies {
  object Versions {
    val circeVersion = "0.6.1"
    val akkaVersion = "2.4.19"
    val akkaHttpVersion = "10.0.9"
    val catsVersion = "0.8.1"
    val scalaTestVersion = "3.0.3"
    val akkaHttpCirceVersion = "1.11.0"
    val paradiseVersion = "2.1.0"
  }
  import Versions._

  val cats = "org.typelevel" %% "cats" % catsVersion

  object akka {
    val http = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
    val httpCirce = "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion
    val httpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  }

  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion

  object circe {
    val core = "io.circe" %% "circe-core" % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
    val parser = "io.circe" %% "circe-parser" % circeVersion
  }
}
