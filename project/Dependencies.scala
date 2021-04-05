import sbt._

object Version {
  val Scala = Seq("2.11.12", "2.12.6", "2.13.5")
  val circe = "0.9.3"
  val akka = "2.5.16"
  val akkaHttp = "10.1.4"
  val akkaHttpCirce = "1.21.0"
  val cats = "1.2.0"
  val scalaTest = "3.0.5"
  val paradise = "2.1.0"
}

object Dependencies {
  object cats {
    val core = "org.typelevel" %% "cats-core" % Version.cats
    val macros = "org.typelevel" %% "cats-macros" % Version.cats
    val kernel = "org.typelevel" %% "cats-kernel" % Version.cats
  }

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
