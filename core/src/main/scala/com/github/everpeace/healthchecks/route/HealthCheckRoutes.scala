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

  def status(s: Boolean)     = if (s) "healthy" else "unhealthy"
  def statusCode(s: Boolean) = if (s) OK else InternalServerError
  def toResultJson(check: HealthCheck, result: HealthCheckResult) =
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
