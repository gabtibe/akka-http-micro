package me.gabrieletiberti.microservice.server
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import scala.concurrent.duration._

trait RestApi {
  implicit val actorSystem = ActorSystem("akka-http-mongodb")
  implicit val actorMaterializer = ActorMaterializer()

  implicit val logger = Logger("Simple Akka Http App")

  implicit val timeout = Timeout(1.minute)

}
