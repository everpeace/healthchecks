import Dependencies._
import sbt.addCompilerPlugin

lazy val root = (project in file("."))
  .aggregate(core, k8sProbes)
  .settings(
    inThisBuild(
      List(
        organization := "com.github.everpeace",
        version := "0.1.0-SNAPSHOT",
        scalaVersion := "2.11.11",
        crossScalaVersions := Seq("2.11.11", "2.12.4"),
        scalafmtOnCompile := true,
        scalafmtTestOnCompile := true,
        addCompilerPlugin(
          "org.scalamacros" % "paradise" % Versions.paradiseVersion cross CrossVersion.full)
      )),
    name := "healthchecks-root",
    publishLocal := {},
    publish := {}
  )

lazy val core = (project in file("core")).settings(
  name := "healthchecks-core",
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
)

lazy val k8sProbes = (project in file("k8s-probes"))
  .settings(
    name := "healthchecks-k8s-probes"
  )
  .dependsOn(core % "test->test;compile->compile")
