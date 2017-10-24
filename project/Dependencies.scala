import sbt._

object Version {
  val Scala = Seq("2.11.11", "2.12.4")
  val circe = "0.6.1"
  val akka = "2.4.19"
  val akkaHttp = "10.0.9"
  val cats = "0.8.1"
  val scalaTest = "3.0.3"
  val akkaHttpCirce = "1.11.0"
  val paradise = "2.1.0"
}

object Dependencies {
  val cats = "org.typelevel" %% "cats" % Version.cats

  object akka {
    val http = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
    val httpCirce = "de.heikoseeberger" %% "akka-http-circe" % Version.akkaHttpCirce
    val httpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
  }

  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest

  object circe {
    val core = "io.circe" %% "circe-core" % Version.circe
    val generic = "io.circe" %% "circe-generic" % Version.circe
    val parser = "io.circe" %% "circe-parser" % Version.circe
  }
}
