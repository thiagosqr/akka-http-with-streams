package com.github.thiagosqsr.telegramstream.repos
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import org.mongodb.scala._

/**
  * Created by thiago on 5/27/16.
  */
class LunchBrakeRepo extends MongoRepo {
  override def collectionName: String = "lunchbrakes"

  private val format = new java.text.SimpleDateFormat("dd-MM-yyyy")

  def insert(l: LunchBrake): Observable[Completed] = {
    val d = Document("_id" -> l.id, "employee" -> l.employee, "body" -> l.body, "start" -> "01/01/2001")
    collection.insertOne(d)
  }

}
