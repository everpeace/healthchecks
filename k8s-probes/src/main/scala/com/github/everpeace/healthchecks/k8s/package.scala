package com.github.everpeace.healthchecks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

package object k8s {

  private def configPathRoot = "k8s_probe"

  private def config(system: ActorSystem, subPath: String = "") = {
    val path =
      if (subPath.nonEmpty) configPathRoot + "." + subPath else configPathRoot
    system.settings.config.getConfig(path)
  }

  def livenessProbe(
      checks: HealthCheck*
    )(implicit
      system: ActorSystem,
      ec: ExecutionContext
    ) = {
    LivenessProbe(checks.toList, config(system, "path").getString("liveness"), ec)
  }

  def readinessProbe(
      checks: HealthCheck*
    )(implicit
      system: ActorSystem,
      ec: ExecutionContext
    ) = {
    ReadinessProbe(checks.toList, config(system, "path").getString("readiness"), ec)
  }

  def bindAndHandleProbes(
      probe: K8sProbe,
      probes: K8sProbe*
    )(implicit
      system: ActorSystem,
      am: ActorMaterializer
    ): Future[Http.ServerBinding] = {
    val host = config(system).getString("host")
    val port = config(system).getInt("port")
    val routes = (probe +: probes).toList
      .map(_.toRoute)
      .reduce((r1: Route, r2: Route) => r1 ~ r2)
    Http(system).bindAndHandle(routes, host, port)
  }
}
