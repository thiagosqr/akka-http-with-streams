package com.github.thiagosqsr.telegramstream.usage

import java.util.UUID
import java.util.concurrent.{ConcurrentMap, TimeUnit}

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.pattern.ask
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import akka.util.Timeout
import com.github.thiagosqsr.telegramstream.PublisherService
import com.github.thiagosqsr.telegramstream.PublisherService.respond
import com.github.thiagosqsr.telegramstream.actors.MongoDataPublisher.Publish
import com.github.thiagosqsr.telegramstream.actors.TelegramActor
import com.github.thiagosqsr.telegramstream.actors.TelegramActor.SendTelegram
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.github.thiagosqsr.telegramstream.repos.RepositoriesModule
import com.google.common.collect.MapMaker
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util.Try

trait DataService {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  implicit val timeout: Timeout = Timeout(1 seconds)

  def dataPublisherRef: ActorRef

  def id = UUID.randomUUID().toString

  def routes: Route = {
    path("data") {
      (post & entity(as[String]) & parameter('sender)) {
        (dataAsString, sender: String) => {
          complete {
            respond(dataPublisherRef ? Publish(LunchBrake(id, sender, dataAsString, DateTime.now)))
          }
        }
      }
    }
  }
}

/**
 * Server that runs our service
 */
object TelegramServer extends App with DataService with PublisherService[LunchBrake]
                                  with RepositoriesModule {

  override implicit lazy val system = ActorSystem()
  override implicit lazy val executor = system.dispatcher
  override implicit lazy val materializer = ActorFlowMaterializer()

  val lunchBrakesCache:ConcurrentMap[String,DateTime] = new MapMaker()
    .concurrencyLevel(4)
    .weakKeys()
    .makeMap()

  val telegramActor = system.actorOf(Props[TelegramActor], "telegramActor")

  override def publisherBufferSize: Int = 1000

  override def dataProcessingDefinition: Sink[LunchBrake, Unit] = Flow[LunchBrake].map(d => {
//  lunchBrakesCache.putIfAbsent(d.employee,d.start)

//    try {
//
//      val f = lunchBrakes.insert(d)
//      //    Await.result(f.toFuture(), Duration(3, TimeUnit.SECONDS))
//      f
//
//    } catch {
//
//      case e: Exception => println(e)
//    }

    val employee = d.employee;

    system.log.info(s"Agendando notificação de almoço para $employee")

    system.scheduler.scheduleOnce(60 seconds, telegramActor, SendTelegram(d.employee))

  }).to(Sink.ignore)

  run()
  val config = ConfigFactory.load()

  import RouteResult._

  Http().bindAndHandle(routes, config.getString("http.host"), config.getInt("http.port"))

}


