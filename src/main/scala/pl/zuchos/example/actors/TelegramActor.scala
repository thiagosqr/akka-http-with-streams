package pl.zuchos.example.actors

import akka.actor.Actor
import akka.actor.Actor.Receive
import pl.zuchos.example.actors.TelegramActor.SendTelegram
import pl.zuchos.example.usage.Data

/**
  * Created by thiago on 5/10/16.
  */
class TelegramActor extends Actor{
  override def receive: Receive = {
    case SendTelegram(d) => println(s"Enviando msg no telegram para $d")
  }
}

object TelegramActor{

  case class SendTelegram(data: Data)

}
