# healthchecks
[![Build Status](https://travis-ci.org/everpeace/healthchecks.svg?branch=master)](https://travis-ci.org/everpeace/healthchecks)
[![Download](https://api.bintray.com/packages/everpeace/maven/healthchecks/images/download.svg)](https://bintray.com/everpeace/maven/healthchecks/_latestVersion)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

tiny healthcheck library for akka-http with [Kubernetes liveness/readiness probe][k8sprobe] support.

## Installation
You need to activate [sbt-bintray](https://github.com/sbt/sbt-bintray) plugin first. And then, You will add it to your build by including these lines in your sbt file.  Please refer to download badge above for the latest version.

```scala
resolvers += Resolver.bintrayRepo("everpeace","maven")
  
libraryDependencies += "com.github.everpeace" %% "healthchecks-core" % <version>
  
// when you want kubernetes liveness/readiness probe support.
libraryDependencies += "com.github.everpeace" %% "healthchecks-k8s-probes" % <version>
```

## Getting Started
### Simple healthcheck endpoint
All you need to give is just health check function returning cats `ValidationNel[String, Unit]`.

```scala
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.HttpRequest
  import cats.syntax.validated._
  import scala.concurrent.Future
  import scala.util.Random

  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher

  import com.github.everpeace.healthchecks._
  import com.github.everpeace.healthchecks.route._

  // defining sync/async healthchecks
  val simple = healthCheck(name = "simple") {
    if (Random.nextBoolean()) healthy else "Unlucky!".invalidNel
  }

  val simpleAsync = asyncHealthCheck("simpleAsync") {
    Future {
      if (Random.nextBoolean()) healthy else "Unlucky!".invalidNel
    }
  }

  // start web server listening "localhost:8888/health"
  val serverBinding = Http().bindAndHandle(
    handler = HealthCheckRoutes.health(simple, simpleAsync),
    interface = "localhost",
    port = 8888
  )

  val response = Http().singleRequest(HttpRequest(uri = "http://localhost:8888/health"))

  // status code is 200(OK) if healthy, 500(Internal Server Error) if unhealthy.
  // response body is empty by default for performance.
  // pass '?full=true' query parameter to see full check result as json. it would be similar to below.
  // Please see com.github.everpeace.healthchecks.HealthRoutesTest for various response patterns.
  // {
  //   "status": "healthy",
  //   "check_results": [
  //     { "name": "simple", "severity": "Fatal", "status": "healthy", "messages": [] },
  //     { "name": "simpleAsync", "severity": "Fatal", "status": "healthy", "messages": [] }
  //   ]
  // }
```

### Kubernetes liveness/readiness probe endpoints
It supports to setup kubernetes liveness/readiness probe really easily like this.  You con configure probe paths and binding setting by typesafe config (i.e. application.conf).  Please refer [reference.conf](k8s-probes/src/main/resources/reference.conf) for details.

```scala
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import scala.concurrent.Future

  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher

  import com.github.everpeace.healthchecks._
  import com.github.everpeace.healthchecks.k8s._
  
  // by default, listening localhost:8086
  // and probe paths are
  //   GET /live
  //   GET /ready
  val probeBinding = bindAndHandleProbes(
    readinessProbe(healthCheck(name = "readiness_check")(healthy)),
    livenessProbe(asyncHealthCheck(name = "liveness_check")(Future(healthy)))
  )
```

Then you can set kubernetes liveness/readiness probe in the kubernetes manifest like below:

```yaml
...
  livenessProbe:
    httpGet:
      path: /live
      port: 8086
      initialDelaySeconds: 3
      periodSeconds: 3
  readinessProbe:
    httpGet:
      path: /ready
      port: 8086
      initialDelaySeconds: 3
      periodSeconds: 3
...
```

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

Please make sure to follow these conventions:
- For each contribution there must be a ticket (GitHub issue) with a short descriptive name, e.g. "Respect host/port configuration setting"
- Work should happen in a branch named "ISSUE-DESCRIPTION", e.g. "32-respect-host-and-port"
- Before a PR can be merged, all commits must be squashed into one with its message made up from the ticket name and the ticket id, e.g. "Respect host/port configuration setting (closes #32)"

## License
This code is open source software licensed under MIT License.

Please note that part of codes in the repository were originally written by [timeoutdigital](https://github.com/timeoutdigital).  Copyright credit presents on relevant sources.

[k8sprobe]: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/ "Kubernetes liveness/readiness probe"
