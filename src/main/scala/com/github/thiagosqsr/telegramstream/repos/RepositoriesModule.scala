package com.github.thiagosqsr.telegramstream.repos

import akka.http.scaladsl.model.DateTime
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.softwaremill.macwire._
import org.mongodb.scala._

/**
  * Created by thiago on 5/27/16.
  */
trait RepositoriesModule {

    lazy val lunchBrakes = wire[LunchBrakeRepo]

//    def toDocument(l: LunchBrake): Document = Document("id" -> 1)
//
//    def toD(d: Document): LunchBrake = LunchBrake("","","",DateTime.now)

}
