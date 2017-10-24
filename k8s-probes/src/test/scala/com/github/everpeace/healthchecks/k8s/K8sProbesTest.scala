package com.chatwork.healthcheck.k8s

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.stream.ActorMaterializer
import com.github.everpeace.healthchecks._
import com.github.everpeace.healthchecks.k8s._
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class K8sProbesTest extends FreeSpec with Matchers {

  private def fixture(probe: K8sProbe, probes: K8sProbe*) = new {}

  "K8sProbes" - {
    "should start successfully and return correct response" in {
      implicit val system = ActorSystem()
      implicit val am     = ActorMaterializer()
      implicit val ec     = system.dispatcher

      val probeBinding = bindAndHandleProbes(
        readinessProbe(healthCheck("readiness_check")(healthy)),
        livenessProbe(asyncHealthCheck("liveness_check")(Future(healthy)))
      )

      def requestToLivenessProbe =
        Http().singleRequest(HttpRequest(uri = "http://localhost:9999/k8s/liveness_probe"))
      def requestToReadinessProbe =
        Http().singleRequest(HttpRequest(uri = "http://localhost:9999/k8s/readiness_probe"))

      val livenessResponse = Await.result(requestToLivenessProbe, 10 seconds)
      val redinessResponse = Await.result(requestToReadinessProbe, 10 seconds)

      livenessResponse.status shouldEqual StatusCodes.OK
      redinessResponse.status shouldEqual StatusCodes.OK

      system.terminate()
    }
  }
}
