package me.gabrieletiberti.microservice.db

import com.typesafe.config.ConfigFactory
import me.gabrieletiberti.microservice.models.User
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

object MongoDb {
  lazy val config = ConfigFactory.load()
  lazy val mongoDbClient = MongoClient(config.getString("mongodb.uri"))
  lazy val codecRegistry =
    fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY)
  lazy val database: MongoDatabase = mongoDbClient
    .getDatabase(config.getString("mongodb.database"))
    .withCodecRegistry(codecRegistry)

  lazy val userCollection: MongoCollection[User] =
    database.getCollection[User]("users")
}
