package me.gabrieletiberti.microservice.server
import me.gabrieletiberti.microservice.controllers.UserController
import me.gabrieletiberti.microservice.db.MongoDb
import me.gabrieletiberti.microservice.services.UserService
import akka.http.scaladsl.server.Directives._

trait Dependencies {
  lazy val userController = new UserController(new UserService(MongoDb.userCollection))

  lazy val routes = (pathPrefix("v1")) {
    userController.routes
  }

}
