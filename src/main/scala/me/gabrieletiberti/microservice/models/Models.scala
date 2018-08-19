package me.gabrieletiberti.microservice.models

import io.circe._
import io.circe.syntax._

import org.bson.types.ObjectId

case class User(id: ObjectId, username: String, age: Int) {
  require(username != null, "Username cannot be null")
  require(username.nonEmpty, "Username cannot be empty")
  require(age > 0, "Age must be positive")
}

object User {
  implicit val encoder: Encoder[User] = (a: User) => {
    Json.obj(
      "id" -> a.id.toHexString.asJson,
      "username" -> a.username.asJson,
      "age" -> a.age.asJson
    )
  }

  implicit val decoder: Decoder[User] = (c: HCursor) => {
    for {
      username <- c.downField("username").as[String]
      age <- c.downField("age").as[Int]
    } yield User(ObjectId.get(), username, age)
  }
}

case class Message(message: String)

object Message {
  implicit val encoder: Encoder[Message] = m =>
    Json.obj("message" -> m.message.asJson)
}

case class FindByIdRequest(id: String) {
  require(ObjectId.isValid(id), "Required Id does not exist")
}