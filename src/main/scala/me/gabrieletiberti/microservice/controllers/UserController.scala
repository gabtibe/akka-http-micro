package me.gabrieletiberti.microservice.controllers

import me.gabrieletiberti.microservice.services.UserService
import me.gabrieletiberti.microservice.services.CustomExecutorService.executorService
import me.gabrieletiberti.microservice.models.{FindByIdRequest, Message, User}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

import scala.util._

class UserController(userService: UserService) {

  val routes =
    pathPrefix("users") {
      (get & path(Segment).as(FindByIdRequest)) { request =>
        onComplete(userService.findById(request.id)) {
          case Success(Some(user)) =>
            complete(Marshal(user).to[ResponseEntity].map { e =>
              HttpResponse(entity = e)
            })
          case Success(None) =>
            complete(HttpResponse(status = StatusCodes.NotFound))
          case Failure(e) =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map {
              e: ResponseEntity =>
                HttpResponse(entity = e,
                             status = StatusCodes.InternalServerError)
            })
        }
      } ~ (post & pathEndOrSingleSlash & entity(as[User])) { user =>
        onComplete(userService.storeUser(user)) {
          case Success(id) =>
            complete(
              HttpResponse(status = StatusCodes.Created,
                           headers = List(Location(s"/v1/users/$id"))))
          case Failure(e) =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map {
              e =>
                HttpResponse(entity = e,
                             status = StatusCodes.InternalServerError)
            })
        }
      }
    }
}
