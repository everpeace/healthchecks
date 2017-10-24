package com.github.everpeace.healthchecks.k8s

import com.github.everpeace.healthchecks.HealthCheck
import com.github.everpeace.healthchecks.route.HealthCheckRoutes

import scala.concurrent.ExecutionContext

sealed abstract class K8sProbe protected (
    val checks: List[HealthCheck],
    val path: String,
    val ec: ExecutionContext) {
  require(checks.nonEmpty, "checks must not be empty.")

  def toRoute = HealthCheckRoutes.health(checks, path)(ec)
}

case class LivenessProbe protected (
    override val checks: List[HealthCheck],
    override val path: String,
    override val ec: ExecutionContext)
    extends K8sProbe(checks, path, ec)

case class ReadinessProbe protected (
    override val checks: List[HealthCheck],
    override val path: String,
    override val ec: ExecutionContext)
    extends K8sProbe(checks, path, ec)
