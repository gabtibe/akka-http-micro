package me.gabrieletiberti.microservice
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.fakemongo.async.FongoAsync
import com.mongodb.async.client.MongoDatabase
import com.typesafe.config.ConfigFactory
import me.gabrieletiberti.microservice.controllers.UserController
import me.gabrieletiberti.microservice.db.MongoDb
import me.gabrieletiberti.microservice.models.User
import me.gabrieletiberti.microservice.services.UserService
import org.mongodb.scala.MongoCollection
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, Matchers}

class MainIntegrationTest
    extends FeatureSpec
    with Matchers
    with ScalatestRouteTest
    with BeforeAndAfterAll {

  val db: MongoDatabase = {
    val fongo = new FongoAsync("akka-http-micro")
    val fongoDb =
      fongo.getDatabase(ConfigFactory.load().getString("mongodb.database"))
    fongoDb.withCodecRegistry(MongoDb.codecRegistry)
  }

  val repository: UserService = new UserService(
    MongoCollection(db.getCollection("col", classOf[User])))

  val routes = Route.seal(new UserController(repository).routes)

  val httpEntity = (str: String) =>
    HttpEntity(ContentTypes.`application/json`, str)

  feature("user api") {
    scenario("success post request") {
      val validUser =
        """
          |{
          | "username": "fafafpmafa",
          | "age": 30
          |}
        """.stripMargin

      Post("/v1/users", httpEntity(validUser)) ~> routes ~> check {
        status shouldBe StatusCodes.Created
      }
    }

    scenario("get same user") {
      val validUser =
        """
          |{
          | "username": "fafafpmafa",
          | "age": 30
          |}
        """.stripMargin

      Post("/v1/users", httpEntity(validUser)) ~> routes ~> check {
        status shouldBe StatusCodes.Created

        Get(header("Location").orNull.value()) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }

    scenario("invalid id on get") {
      Get(s"/api/users/1") ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("no body") {
      Post(s"/api/users", httpEntity("{}")) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("body without age") {
      val invalidUser =
        """
        {
          "username": "gabfssilva"
        }
        """

      Post(s"/api/users", httpEntity(invalidUser)) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("body without username") {
      val invalidUser =
        """
        {
          "age": 24
        }
        """

      Post(s"/api/users", httpEntity(invalidUser)) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
  }
}
