package com.github.thiagosqsr.telegramstream.actors

import akka.actor.Actor.Receive
import akka.stream.actor.ActorPublisher

/**
  * Created by thiago on 5/30/16.
  */
class MongoDataPublisher[D] extends ActorPublisher[D] {
  override def receive: Receive = ???
}

class BufferOverflow extends Exception

object MongoDataPublisher {

  case class Publish[D](data: D)

}