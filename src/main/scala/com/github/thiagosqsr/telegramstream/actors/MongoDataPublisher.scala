package com.github.thiagosqsr.telegramstream.actors

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import com.github.thiagosqsr.telegramstream.actors.MongoDataPublisher.Publish
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.github.thiagosqsr.telegramstream.repos.RepositoriesModule
import org.mongodb.scala._

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

  private def cacheIfPossible(d: LunchBrake) {

      val o = lunchBrakes.collection.insertOne(lunchBrakes.toDocument(d))
      val s: ActorRef = sender()

      o.subscribe(
          (l: Completed) => s ! Success(),
          (error: Throwable) => s ! Failure(error)
      )

      publishIfNeeded()
  }

  def publishIfNeeded() = {

    val all = lunchBrakes.collection.find()

    all.subscribe(
      (d: Document) => {

        val lunchBrake = lunchBrakes.toLunchBrake(d)

        if(!lunchBrake.isEmpty && (isActive && totalDemand > 0)){
          onNext(lunchBrake.get)
          log.info(s"Removendo notificação com ID $lunchBrake.")
          lunchBrakes.collection.deleteOne(d).toFuture()
        }
      },
      (error: Throwable) => log.error(error,error.getMessage)

    )

    //    val o = lunchBrakes.collection.count()
//    var count: Long = 0
//
//    o.subscribe(
//      (c: Long) => count = c,
//      (error: Throwable) => log.error(error, "MongoDB fetch error"),
//      () => log.debug("Done fetching from MongoDB")
//    )
//
//      Await.result(all.toFuture(), Duration(5, TimeUnit.SECONDS))

//    while (iterator.hasNext && isActive && totalDemand > 0) {
//      val d: = iterator.next().asInstanceOf[Document]
//      onNext(toD(d))
//    }
  }

}

object MongoDataPublisher {

  case class Publish(data: LunchBrake)

}
