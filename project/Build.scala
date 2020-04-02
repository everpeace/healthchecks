import bintray.BintrayPlugin
import bintray.BintrayPlugin.autoImport._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._
import sbt.Keys._
import sbt.{AutoPlugin, CrossVersion, _}
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {
  override def requires: Plugins = JvmPlugin && HeaderPlugin && BintrayPlugin

  override def trigger = allRequirements

  override def projectSettings =
    Seq(
      // Core settings
      organization := "com.github.everpeace",
      organizationName := "Shingo Omura",
      startYear := Some(2017),
      licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
      headerLicense := Some(HeaderLicense.MIT("2017", "Shingo Omura")),
      homepage := Some(url("https://github.com/everpeace/healthchecks")),
      pomIncludeRepository := (_ => false),
      pomExtra := <scm>
      <url>https://github.com/everpeace/healthchecks</url>
      <connection>scm:git:git@github.com:everpeace/healthchecks</connection>
    </scm>
      <developers>
        <developer>
          <id>everpeace</id>
          <name>Shingo Omura</name>
          <url>http://everpeace.github.io/</url>
        </developer>
      </developers>,
      scalaVersion := Version.Scala.head,
      crossScalaVersions := Version.Scala,
      scalacOptions ++= Vector(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-encoding",
        "UTF-8"
//      "-Ywarn-unused-import",
      ),
      scalacOptions ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n >= 13 => "-Ymacro-annotations" :: Nil
          case _                       => Nil
        }
      },
      libraryDependencies ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n >= 13 => Nil
          case _ =>
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
        }
      },
      // Scalafmt setting
      scalafmtOnCompile := true,
      // Bintray settings
      bintrayPackage := "healthchecks",
      bintrayRepository := "maven",
      releaseCrossBuild := true,
      releaseVersionBump := sbtrelease.Version.Bump.Next
    )
}
