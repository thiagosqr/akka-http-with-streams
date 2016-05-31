package com.github.thiagosqsr.telegramstream.repos

import com.typesafe.config.ConfigFactory
import org.mongodb.scala._

/**
  * Created by thiago on 5/27/16.
  */
trait MongoRepo {

  val config = ConfigFactory.load()

  private lazy val client: MongoClient = MongoClient(config.getString("mongo.url"))

  private lazy val database: MongoDatabase = client.getDatabase(config.getString("mongo.database"))

  def collection: MongoCollection[Document] = database.getCollection(collectionName)

  def collectionName: String

}

