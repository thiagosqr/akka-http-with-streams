package com.github.thiagosqsr.telegramstream

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes.{Accepted, ServiceUnavailable}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.actor.ActorSubscriber
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{FlowMaterializer, OperationAttributes}
import com.github.thiagosqsr.telegramstream.msgs.LunchBrake
import com.github.thiagosqsr.telegramstream.usage.{DataService, TelegramServer}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

import scala.concurrent.ExecutionContextExecutor

class SimpleServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach {

  val that = this
  var simpleService: DataService with PublisherService[LunchBrake] = _

  override protected def beforeEach(): Unit = {
    simpleService = new DataService with PublisherService[LunchBrake] {
      
      lazy val dataSubscriberRef = system.actorOf(Props[LazyDataSubscriber](new LazyDataSubscriber()))
      lazy val dataSubscriber = ActorSubscriber[LunchBrake](dataSubscriberRef)
      
      override implicit lazy val system: ActorSystem = that.system
      override implicit lazy val materializer: FlowMaterializer = that.materializer
      override implicit def executor: ExecutionContextExecutor = that.executor
      override def publisherBufferSize: Int = 2
      override def dataProcessingDefinition: Sink[LunchBrake, Unit] = Flow[LunchBrake].map(d => {
        println(s"Processing data from ${d.employee} body: ${d.body}")
        d
      }).to(Sink(dataSubscriber)).withAttributes(OperationAttributes.inputBuffer(1, 1))
    }
    simpleService.run()

  }

  "SimpleService" should {
    "respond with Data received" in {
      Post("/data?sender=Lukasz", "Test1") ~> simpleService.routes ~> check {
        status shouldBe Accepted
        val entity: String = entityAs[String]
        entity shouldBe "Data received"
      }
    }
    "respond with Data received until service will become unavailable" in {
      Post("/data?sender=Lukasz", "Test1") ~> simpleService.routes ~> check {
        status shouldBe Accepted
        val entity: String = entityAs[String]
        entity shouldBe "Data received"
      }
      Post("/data?sender=Lukasz", "Test2") ~> simpleService.routes ~> check {
        status shouldBe Accepted
        val entity: String = entityAs[String]
        entity shouldBe "Data received"
      }
      Post("/data?sender=Lukasz", "Test3") ~> simpleService.routes ~> check {
        status shouldBe Accepted
        val entity: String = entityAs[String]
        entity shouldBe "Data received"
      }
      Post("/data?sender=Lukasz", "Test4") ~> simpleService.routes ~> check {
        status shouldBe ServiceUnavailable
        val entity: String = entityAs[String]
        entity shouldBe "Try again later..."
      }
    }
  }
}

