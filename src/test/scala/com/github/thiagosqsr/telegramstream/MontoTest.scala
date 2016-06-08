package com.github.thiagosqsr.telegramstream


//import com.mongodb.ServerAddress
//import com.mongodb.connection.ClusterSettings
//import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase}
//import org.mongodb.scala.bson.collection.immutable.Document
//
import java.util.UUID
import java.util.concurrent.TimeUnit

import org.mongodb.scala._

import scala.io.StdIn
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
  * Created by thiago on 5/18/16.
  */
object MontoTest extends App{

  import ExecutionContext.Implicits.global

  val mongoClient: MongoClient =  MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("telegramstream")
  val collection: MongoCollection[Document] = database.getCollection("lunchbrakes")

  val id = UUID.randomUUID().toString

  // The Document ADT enforces type safety and can implicitly box native scala types to BSON types
  val query = Document("_id" -> id)  // "Martin" becomes BsonString("Martin")

  val d = Document("_id" -> id, "employee" -> "thiago", "body" -> "", "start" -> "01/01/2001")
  val insert = collection.insertOne(d)

  Await.result(insert.toFuture(), Duration(10, TimeUnit.SECONDS))

  // Lets run a query for all Martins and print out the json representation of each document
  collection.find(Document("employee" -> "thiago")).subscribe(
    (user: Document) => println(user.toJson()),                         // onNext
    (error: Throwable) => println(s"Query failed: ${error.getMessage}"), // onError
    () => println("Done")                                               // onComplete
  )

//  StdIn.readLine();

  Await.result(Future{}, Duration(1, TimeUnit.SECONDS))

}
