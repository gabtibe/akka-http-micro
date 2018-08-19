package me.gabrieletiberti.microservice.services

import me.gabrieletiberti.microservice.models.User
import me.gabrieletiberti.microservice.services.CustomExecutorService._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.Future

class UserService(collection: MongoCollection[User]) {
  def findById(id: String): Future[Option[User]] =
    collection
      .find(Document("id" -> new ObjectId(id)))
      .first
      .head
      .map(maybeUser => Option(maybeUser))

  def storeUser(user: User): Future[String] =
    collection.insertOne(user).head.map(_ => user.id.toHexString)
}
