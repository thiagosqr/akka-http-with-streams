package com.github.thiagosqsr.telegramstream.actors

import akka.actor.{Actor, ActorRef}
import com.github.thiagosqsr.telegramstream.actors.TelegramActor.SendTelegram

import scala.util.Success

/**
  * Created by thiago on 5/10/16.
  */
class TelegramActor extends Actor{

  def sendMsg(sender: ActorRef, employee: String): Unit = {
    this.context.system.log.info(s"Enviando msg no telegram para $employee")
    sender ! Success()
  }

  override def receive: Receive = {
    case SendTelegram(d) => sendMsg(sender(),d)
  }
}

object TelegramActor{

  case class SendTelegram(msgId: String)

}
