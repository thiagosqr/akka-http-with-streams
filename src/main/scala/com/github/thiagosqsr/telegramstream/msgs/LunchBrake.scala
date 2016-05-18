package com.github.thiagosqsr.telegramstream.msgs

import akka.http.scaladsl.model.DateTime

/**
  * Created by thiago on 5/17/16.
  */
case class LunchBrake(employee: String, body: String, start: DateTime)
