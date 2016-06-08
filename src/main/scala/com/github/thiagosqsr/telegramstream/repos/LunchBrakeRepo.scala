package com.github.thiagosqsr.telegramstream.repos
import java.util.Date

import akka.http.scaladsl.model.DateTime
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import org.mongodb.scala._

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
    Document("_id" -> l.id, "employee" -> l.employee, "body" -> l.body, "start" -> format.format(new Date()))
  }

  def toLunchBrake(d: Document): LunchBrake = LunchBrake("1","thiago","{}",DateTime.now)


}
