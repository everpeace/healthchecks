lazy val root = project
  .in(file("."))
  .settings(
    publishLocal := {},
    publish := {}
  )
  .aggregate(core, k8sProbes)

lazy val core = project
  .in(file("core"))
  .enablePlugins(AutomateHeaderPlugin)

lazy val k8sProbes = project
  .in(file("k8s-probes"))
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core % "test->test;compile->compile")
