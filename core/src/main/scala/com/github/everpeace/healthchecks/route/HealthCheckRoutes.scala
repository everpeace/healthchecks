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

/*
 * The original source code were written by https://github.com/timeoutdigital.
 * Original source code are distributed with MIT License.
 * Please see: https://github.com/timeoutdigital/akka-http-healthchecks
 * The codes are modified from original one by Shingo Omura.
 */

package com.github.everpeace.healthchecks.route

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers
import cats.data.Validated.{Invalid, Valid}
import com.github.everpeace.healthchecks.{HealthCheck, HealthCheckResult}
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.JsonCodec
import io.circe.generic.auto._

import scala.collection.convert.DecorateAsScala
import scala.concurrent.{ExecutionContext, Future}

object HealthCheckRoutes extends DecorateAsScala {

  @JsonCodec case class HealthCheckResultJson(
      name: String,
      severity: String,
      status: String,
      messages: List[String])
  @JsonCodec case class ResponseJson(status: String, check_results: List[HealthCheckResultJson])

  private def status(s: Boolean)     = if (s) "healthy" else "unhealthy"
  private def statusCode(s: Boolean) = if (s) OK else InternalServerError
  private def toResultJson(check: HealthCheck, result: HealthCheckResult) =
    HealthCheckResultJson(
      check.name,
      check.severity.toString,
      status(result.isValid),
      result match {
        case Valid(_)        => List()
        case Invalid(errors) => errors.toList
      }
    )

  def health(
      checks: List[HealthCheck],
      pathString: String = "health"
    )(implicit
      ec: ExecutionContext
    ) = {
    require(checks.nonEmpty, "checks must not empty.")
    require(
      checks.toList.map(_.name).toSet.size == checks.toList.length,
      s"HealthCheck name should be unique (given HealthCheck names = [${checks.toList.map(_.name).mkString(",")}])."
    )
    val rootSlashRemoved =
      if (pathString.startsWith("/")) pathString.substring(1) else pathString
    path(PathMatchers.separateOnSlashes(rootSlashRemoved)) {
      get {
        complete {
          Future
            .traverse(checks.toList) { c =>
              c.run().map(c -> _)
            }
            .map { checkAndResults =>
              val healthy = checkAndResults.forall(cr => cr._2.isValid || (!cr._1.severity.isFatal))
              statusCode(healthy) -> ResponseJson(
                status(healthy),
                checkAndResults.map {
                  case (check, result) => toResultJson(check, result)
                }
              )
            }
        }
      }
    }
  }
}
