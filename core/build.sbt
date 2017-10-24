import Dependencies._

name := "healthchecks-core"

libraryDependencies ++= Seq(
  cats,
  akka.http,
  akka.httpCirce,
  circe.core,
  circe.generic,
  circe.parser
) ++ Seq(
  akka.httpTestKit % "test",
  scalaTest        % "test"
)
