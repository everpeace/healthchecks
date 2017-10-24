/*
 * Copyright (c) 2017 Shingo Omura
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
