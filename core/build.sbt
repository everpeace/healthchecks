import Dependencies._

name := "healthchecks-core"

libraryDependencies ++= Seq(
  cats.core,
  cats.macros,
  cats.kernel,
  akka.actor,
  akka.stream,
  akka.http,
  akka.httpCirce,
  circe.core,
  circe.generic,
  circe.genericExtras,
  circe.parser
) ++ Seq(
  akka.testKit     % Test,
  akka.httpTestKit % Test,
  scalaTest        % Test
) ++ {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2L, scalaMajor)) if scalaMajor == 13 =>
      Seq.empty
    case Some((2L, scalaMajor)) if scalaMajor == 12 =>
      Seq(
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.4"
      )
  }
}
