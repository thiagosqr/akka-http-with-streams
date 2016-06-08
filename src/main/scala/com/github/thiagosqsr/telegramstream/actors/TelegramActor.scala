package com.github.thiagosqsr.telegramstream.actors

import akka.actor.Actor
import com.github.thiagosqsr.telegramstream.actors.TelegramActor.SendTelegram

/**
  * Created by thiago on 5/10/16.
  */
class TelegramActor extends Actor{
  override def receive: Receive = {

    case SendTelegram(d) => this.context.system.log.info(s"Enviando msg no telegram para $d")
  }
}

object TelegramActor{

  case class SendTelegram(msgId: String)

}
