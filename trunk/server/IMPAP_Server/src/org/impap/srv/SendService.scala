package org.impap.srv

import scala.actors.Actor
import scala.actors.Actor._
import java.nio.channels.SocketChannel
import java.io.IOException

class SendService(channel: SocketChannel) extends Actor{
 def act() {
    loop
    {
      react
      {
        case SendDataMessage(tag, text) =>
          try{
            if(channel.isConnected){
              // Console.print("S: " + text)
              channel.socket.getOutputStream.write((tag + " " + text + IMAPConstants.EOL).getBytes())
              //Console.println("Send complete")
            }
            else{
              Console.println("Shit happened")
            }
          }
          catch{
             case e: IOException =>
               Console.println("IO Error happened")
               sender ! Stop
          }

        case SendLastDataMessage(tag, text) =>
          if(channel.isConnected)
          {
            //Console.print("S: " + text)
            channel.socket.getOutputStream.write((tag + " " + text + IMAPConstants.EOL).getBytes())
            //Console.println("Send complete")
            sender ! Stop
          }
          else
          {
            Console.println("Shit happened")
          }

        case Stop =>
          Console.println("stopping send service")
          exit()
      }
    }
  }  
}

