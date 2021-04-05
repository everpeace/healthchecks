lazy val root = project
  .in(file("."))
  .settings(
    publishLocal := {},
    publish := {}
  )
  .aggregate(core, k8sProbes)

lazy val core = project
  .enablePlugins(AutomateHeaderPlugin)
  .in(file("core"))

lazy val k8sProbes = project
  .in(file("k8s-probes"))
  .settings(
    name := "k8s-probes"
  )
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core % "test->test;compile->compile")
