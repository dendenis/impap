package org.impap.srv

import scala.actors.Actor
import scala.actors.Actor._
import java.nio.channels.SocketChannel;

class SendService(channel: SocketChannel) extends Actor{
 def act() {
    loop
    {
      react
      {
        case SendDataMessage(text) =>
          if(channel.isConnected)
          {
            Console.print("S: " + text)
            channel.socket.getOutputStream.write(text.getBytes())
            Console.println("Send complete")
          }
          else
          {
            Console.println("Shit happened")
            this ! Stop    
          }
        case SendLastDataMessage(text) =>
          if(channel.isConnected)
          {
            Console.print("S: " + text)
            channel.socket.getOutputStream.write(text.getBytes())
            Console.println("Send complete")
            sender ! Stop
          }
          else
          {
            Console.println("Shit happened")
            this ! Stop
          }

        case Stop =>
          Console.println("stopping send service")
          exit()
      }
    }
  }  
}

