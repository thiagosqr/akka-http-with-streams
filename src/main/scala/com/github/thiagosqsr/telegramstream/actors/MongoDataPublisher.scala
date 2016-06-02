package com.github.thiagosqsr.telegramstream.actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import com.github.thiagosqsr.telegramstream.actors.DataPublisher.Publish
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.github.thiagosqsr.telegramstream.repos.RepositoriesModule
import org.mongodb.scala._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by thiago on 5/30/16.
  */
class MongoDataPublisher extends ActorPublisher[LunchBrake] with RepositoriesModule {

  val log = Logging(context.system, this)

  override def receive: Actor.Receive = {
    case Publish(l: LunchBrake) =>
      cacheIfPossible(l)
    case Request(cnt) =>
      publishIfNeeded()
    case Cancel =>
      context.stop(self)
    case _ =>
  }

  private def cacheIfPossible(l: LunchBrake) {

      val o = lunchBrakes.insert(l)
      val s: ActorRef = sender()

      o.subscribe(
          (l: Completed) => s ! Success(),
          (error: Throwable) => s ! Failure(error),
          () => log.debug("Done inserting into MongoDB")
      )

      Await.result(o.toFuture(), Duration(3, TimeUnit.SECONDS))
      publishIfNeeded()
  }

  def publishIfNeeded() = {

    val iterator = lunchBrakes.collection.find().productIterator


//    val o = lunchBrakes.collection.count()
//    var count: Long = 0
//
//    o.subscribe(
//      (c: Long) => count = c,
//      (error: Throwable) => log.error(error, "MongoDB fetch error"),
//      () => log.debug("Done fetching from MongoDB")
//    )
//
//    Await.result(o.toFuture(), Duration(3, TimeUnit.SECONDS))

    while (iterator.hasNext && isActive && totalDemand > 0) {

      val n = iterator.next()


//      onNext(queue.dequeue())
    }
  }
}

object MongoDataPublisher {

  case class Publish(data: LunchBrake)

}