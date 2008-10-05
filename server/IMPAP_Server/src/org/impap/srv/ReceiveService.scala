package org.impap.srv

import scala.actors.Actor
import scala.actors.Actor._
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.nio.channels.AsynchronousCloseException
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException
import java.nio.channels.ClosedChannelException

class ReceiveService(owner: Actor, channel: SocketChannel) extends Actor{
  var buffer = new StringBuffer() 
  def act() {
    loop
    {
      react
      {
        case ReceiveCommand =>
          try{
            if(channel.isConnected)
            {
              val reader = new BufferedReader(new InputStreamReader(channel.socket.getInputStream))
              val ch = reader.read.toChar
              buffer.append(ch)
              if(ch == 13)
              {
                val command = buffer.toString;
                buffer = new StringBuffer()
                owner ! ReceivedDataMessage(command.trim)
              }
              this ! ReceiveCommand
            }    
            else
            {
              Console.println("Shit happened")
              this ! Stop    
            }
          }  
          catch
          { 
             case e: AsynchronousCloseException => 
               Console.println("Asynchronous Shit happened")
               this ! Stop
             case e: SocketTimeoutException =>
               this ! ReceiveCommand
             case e: CancelledKeyException =>
               Console.println("Cancelled Shit happened")
               this ! Stop
             case e: ClosedChannelException =>
               Console.println("Closed Shit happened")
               this ! Stop

          }
        case Stop =>
          Console.println("stopping receive service")
          exit()
      }
    }
  }  
}
