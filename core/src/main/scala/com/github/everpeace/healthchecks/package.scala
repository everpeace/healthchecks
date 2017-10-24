package com.github.everpeace

import cats.data.ValidatedNel
import cats.syntax.validated._
import com.github.everpeace.healthchecks.HealthCheck.Severity

import scala.concurrent.Future
import scala.util.Try

package object healthchecks {
  type HealthCheckResult = ValidatedNel[String, Unit]

  def healthy: HealthCheckResult = ().validNel[String]

  def unhealthy(msg: String): HealthCheckResult = msg.invalidNel[Unit]

  def healthCheck(
      name: String,
      severity: Severity = Severity.Fatal
    )(c: => HealthCheckResult
    ): HealthCheck =
    new HealthCheck(name, Future.fromTry(Try(c)), severity)

  def asyncHealthCheck(
      name: String,
      severity: Severity = Severity.Fatal
    )(c: => Future[HealthCheckResult]
    ): HealthCheck =
    new HealthCheck(name, c, severity)
}
