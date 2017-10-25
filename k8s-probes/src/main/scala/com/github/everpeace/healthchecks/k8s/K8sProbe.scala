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

package com.github.everpeace.healthchecks.k8s

import com.github.everpeace.healthchecks.HealthCheck
import com.github.everpeace.healthchecks.route.HealthCheckRoutes

import scala.concurrent.ExecutionContext

sealed abstract class K8sProbe protected (
    val checks: List[HealthCheck],
    val path: String,
    val ec: ExecutionContext) {
  require(checks.nonEmpty, "checks must not be empty.")

  def toRoute = HealthCheckRoutes.health(path, checks)(ec)
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
