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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

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
    ): LivenessProbe = {
    LivenessProbe(checks.toList, config(system, "path").getString("liveness"), ec)
  }

  def readinessProbe(
      checks: HealthCheck*
    )(implicit
      system: ActorSystem,
      ec: ExecutionContext
    ): ReadinessProbe = {
    ReadinessProbe(checks.toList, config(system, "path").getString("readiness"), ec)
  }

  def bindAndHandleProbes(
      probe: K8sProbe,
      probes: K8sProbe*
    )(implicit
      system: ActorSystem
    ): Future[Http.ServerBinding] = {
    val host = config(system).getString("host")
    val port = config(system).getInt("port")
    val routes = (probe +: probes).toList
      .map(_.toRoute)
      .reduce((r1: Route, r2: Route) => r1 ~ r2)
    Http(system).bindAndHandle(routes, host, port)
  }
}
