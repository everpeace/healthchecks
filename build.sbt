lazy val root = project
  .copy(id = "root")
  .in(file("."))
  .settings(
    publishLocal := {},
    publish := {}
  )
  .aggregate(core, k8sProbes)

lazy val core = project
  .copy(id = "core")
  .enablePlugins(AutomateHeaderPlugin)
  .in(file("core"))

lazy val k8sProbes = project
  .copy(id = "k8s-probes")
  .in(file("k8s-probes"))
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core % "test->test;compile->compile")
