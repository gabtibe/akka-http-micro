package me.gabrieletiberti.microservice

import akka.http.scaladsl.Http
import me.gabrieletiberti.microservice.server.{Dependencies, RestApi}
import me.gabrieletiberti.microservice.services.CustomExecutorService.executorService

import scala.util.{Failure, Success}

object Main extends App with RestApi with Dependencies {
  Http().bindAndHandle(routes, "0.0.0.0", 8080).onComplete {
    case Success(value) => logger.info(s"Application is up and running at ${value.localAddress.getHostName}:${value.localAddress.getPort}")
    case Failure(e) => logger.error(s"Error in starting the Application: ${e.getMessage}")
  }
}
