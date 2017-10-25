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

package com.github.everpeace.healthchecks

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.everpeace.healthchecks.HealthCheck.Severity
import com.github.everpeace.healthchecks.route.HealthCheckRoutes
import org.scalatest.{FunSpec, Matchers, OneInstancePerTest}

class HealthRoutesTest
    extends FunSpec
    with ScalatestRouteTest
    with Matchers
    with OneInstancePerTest {

  describe("HealthCheck route") {
    it("should raise exception when no healthcheck is given.") {
      val exception = the[IllegalArgumentException] thrownBy HealthCheckRoutes
        .health()
      exception.getMessage shouldEqual "requirement failed: checks must not empty."
    }

    it("should raise exception when given healthchecks have same names") {
      val exception = the[IllegalArgumentException] thrownBy HealthCheckRoutes.health(
        healthCheck("test")(healthy),
        healthCheck("test")(healthy)
      )
      exception.getMessage shouldEqual "requirement failed: HealthCheck name should be unique (given HealthCheck names = [test,test])."
    }

    it("should return correct healthy response when all healthchecks are healthy.") {
      val ok1 = healthCheck("test1")(healthy)
      val ok2 = healthCheck("test2")(healthy)

      Get("/health") ~> HealthCheckRoutes.health(ok1, ok2) ~> check {
        status shouldEqual OK
        responseAs[String] shouldEqual
          """
            |{
            |  "status": "healthy",
            |  "check_results": [
            |    { "name": "test1", "severity": "Fatal", "status": "healthy", "messages": [] },
            |    { "name": "test2", "severity": "Fatal", "status": "healthy", "messages": [] }
            |  ]
            |}
          """.stripMargin.replaceAll("\n", "").replaceAll(" ", "")
      }
    }

    it(
      "should return correct healthy response when some healthchecks are unhealthy but those are all NonFatal.") {
      val ok1 = healthCheck("test1")(healthy)
      val failedButNonFatal =
        healthCheck("test2", Severity.NonFatal)(unhealthy("error"))

      Get("/health") ~> HealthCheckRoutes.health(ok1, failedButNonFatal) ~> check {
        status shouldEqual OK
        responseAs[String] shouldEqual
          """
            |{
            |  "status": "healthy",
            |  "check_results": [
            |    { "name": "test1", "severity": "Fatal",    "status": "healthy",   "messages": [] },
            |    { "name": "test2", "severity": "NonFatal", "status": "unhealthy", "messages": ["error"] }
            |  ]
            |}
          """.stripMargin.replaceAll("\n", "").replaceAll(" ", "")
      }
    }

    it(
      "should return correct response when some of 'Fatal' healthchecks are unhealthy system with a fatal error") {
      val ok = healthCheck("test1")(healthy)
      val failedButNonFatal =
        healthCheck("test2", Severity.NonFatal)(unhealthy("error"))
      val failedFatal = healthCheck("test3")(throw new Exception("exception"))

      Get("/health") ~> HealthCheckRoutes.health(ok, failedButNonFatal, failedFatal) ~> check {
        status shouldEqual InternalServerError
        responseAs[String] shouldEqual
          """
            |{
            |  "status": "unhealthy",
            |  "check_results": [
            |    { "name": "test1", "severity": "Fatal",    "status": "healthy",   "messages": [] },
            |    { "name": "test2", "severity": "NonFatal", "status": "unhealthy", "messages": ["error"] },
            |    { "name": "test3", "severity": "Fatal",    "status": "unhealthy", "messages": ["exception"] }
            |  ]
            |}
          """.stripMargin.replaceAll("\n", "").replaceAll(" ", "")
      }
    }
  }

}
