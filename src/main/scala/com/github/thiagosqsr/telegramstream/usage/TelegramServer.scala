package com.github.thiagosqsr.telegramstream.usage

import java.util.concurrent.ConcurrentMap

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
import com.github.thiagosqsr.telegramstream.actors.DataPublisher.Publish
import com.github.thiagosqsr.telegramstream.actors.TelegramActor
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.google.common.collect.MapMaker
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

trait DataService {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  implicit val timeout: Timeout = Timeout(1 seconds)

  def dataPublisherRef: ActorRef

  def routes: Route = {
    path("data") {
      (post & entity(as[String]) & parameter('sender)) {
        (dataAsString, sender: String) => {
          complete {
            respond(dataPublisherRef ? Publish(LunchBrake(sender, dataAsString, DateTime.now)))
          }
        }
      }
    }
  }
}

/**
 * Server that runs our service
 */
object TelegramServer extends App with DataService with PublisherService[LunchBrake] {

  override implicit lazy val system = ActorSystem()
  override implicit lazy val executor = system.dispatcher
  override implicit lazy val materializer = ActorFlowMaterializer()

  val lunchBrakes:ConcurrentMap[String,DateTime] = new MapMaker()
    .concurrencyLevel(4)
    .weakKeys()
    .makeMap()

  val telegramActor = system.actorOf(Props[TelegramActor], "telegramActor")

  override def publisherBufferSize: Int = 1000

  override def dataProcessingDefinition: Sink[LunchBrake, Unit] = Flow[LunchBrake].map(d => {
    System.out.println("Registering employee checkout")
    lunchBrakes.putIfAbsent(d.employee,d.start)
  }).to(Sink.ignore)

  run()
  val config = ConfigFactory.load()

  import RouteResult._

  Http().bindAndHandle(routes, config.getString("http.host"), config.getInt("http.port"))

}


