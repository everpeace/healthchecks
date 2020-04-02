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
)
