package com.github.thiagosqsr.telegramstream.repos

import com.softwaremill.macwire._

/**
  * Created by thiago on 5/27/16.
  */
trait RepositoriesModule {

    lazy val lunchBrakes = wire[LunchBrakeRepo]

}
