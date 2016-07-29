package com.github.thiagosqsr.telegramstream.repos

/**
  * Created by thiago on 5/27/16.
  */
trait RepositoriesModule {

    lazy val lunchBrakes = new LunchBrakeRepo()

}
