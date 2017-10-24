package com.github.everpeace.healthchecks

import cats.syntax.validated._
import com.github.everpeace.healthchecks.HealthCheck.Severity

import scala.concurrent.{ExecutionContext, Future}

class HealthCheck(val name: String, check: => Future[HealthCheckResult], val severity: Severity) {
  def run()(implicit ec: ExecutionContext): Future[HealthCheckResult] = {
    check.recover {
      case t: Throwable => t.getMessage.invalidNel[Unit]
    }
  }
}

object HealthCheck {

  sealed trait Severity {
    def isFatal: Boolean
  }

  object Severity {

    case object Fatal extends Severity {
      override def isFatal = true
    }

    case object NonFatal extends Severity {
      override def isFatal = false
    }

  }

}
