package com.github.thiagosqsr.telegramstream.actors

import akka.actor.Actor
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import com.github.thiagosqsr.telegramstream.actors.DataPublisher.Publish

import scala.collection.mutable
import scala.util.{Failure, Success}

class DataPublisher[D](val bufferSize: Int) extends ActorPublisher[D] {

  require(bufferSize >= 0)

  var queue: mutable.Queue[D] = mutable.Queue()

  override def receive: Actor.Receive = {
    case Publish(s: D@unchecked) =>
      cacheIfPossible(s)
    case Request(cnt) =>
      publishIfNeeded()
    case Cancel => context.stop(self)
    case _ =>
  }

  private def cacheIfPossible(s: D) {
    if (queue.length == bufferSize) {
      sender() ! Failure(new BufferOverflow)
    } else {
      queue.enqueue(s)
      sender() ! Success()
      publishIfNeeded()
    }
  }

  def publishIfNeeded() = {
    while (queue.nonEmpty && isActive && totalDemand > 0) {
      onNext(queue.dequeue())
    }
  }
}

class BufferOverflow extends Exception

object DataPublisher {

  case class Publish[D](data: D)

}