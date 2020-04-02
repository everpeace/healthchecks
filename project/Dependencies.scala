import sbt._

object Version {
  val Scala         = Seq("2.13.1", "2.12.10")
  val circe         = "0.13.0"
  val akka          = "2.6.4"
  val akkaHttp      = "10.1.11"
  val akkaHttpCirce = "1.31.0"
  val cats          = "2.1.1"
  val scalaTest     = "3.1.1"
  val paradise      = "2.1.1"
}

object Dependencies {
  val cats = "org.typelevel" %% "cats-core" % Version.cats

  object akka {
    val actor       = "com.typesafe.akka" %% "akka-actor"        % Version.akka
    val stream      = "com.typesafe.akka" %% "akka-stream"       % Version.akka
    val testKit     = "com.typesafe.akka" %% "akka-testkit"      % Version.akka
    val http        = "com.typesafe.akka" %% "akka-http"         % Version.akkaHttp
    val httpCirce   = "de.heikoseeberger" %% "akka-http-circe"   % Version.akkaHttpCirce
    val httpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
  }

  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest

  object circe {
    val core          = "io.circe" %% "circe-core"           % Version.circe
    val generic       = "io.circe" %% "circe-generic"        % Version.circe
    val genericExtras = "io.circe" %% "circe-generic-extras" % Version.circe
    val parser        = "io.circe" %% "circe-parser"         % Version.circe
  }

}
