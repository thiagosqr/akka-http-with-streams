package com.github.thiagosqsr.telegramstream.repos
import java.util.Date

import akka.http.scaladsl.model.DateTime
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import org.bson.BsonString
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonDateTime

import scala.util.Try

/**
  * Created by thiago on 5/27/16.
  */
class LunchBrakeRepo extends MongoRepo {
  override def collectionName: String = "lunchbrakes"

  private val format = new java.text.SimpleDateFormat("dd-MM-yyyy")

  def insert(l: LunchBrake): Observable[Completed] = {
    collection.insertOne(toDocument(l))
  }

  def toDocument(l: LunchBrake): Document = {
    Document("_id" -> l.id, "employee" -> l.employee, "body" -> l.body, "start" -> new Date())
  }

  def toLunchBrake(d: Document): Option[LunchBrake] =
    Try(
      for {
        id <- d.get[BsonString]("_id") map (_.asString().getValue)
        employee <- d.get[BsonString]("employee") map (_.asString().getValue)
        start <- d.get[BsonDateTime]("start") map (dt => DateTime(dt.asDateTime().getValue))
      } yield LunchBrake(id, employee, "", start)
    ).getOrElse(None)
}
