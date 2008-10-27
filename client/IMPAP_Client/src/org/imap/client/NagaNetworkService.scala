package org.imap.client

import scala.actors.Actor
import scala.actors.Actor._

import naga.ConnectionAcceptor
import naga.NIOServerSocket
import naga.NIOService
import naga.NIOSocket
import naga.ServerSocketObserverAdapter
import naga.SocketObserver
import naga.packetreader.DelimiterPacketReader
import java.lang.Integer

class NagaNetworkService(applicationClient: Actor) extends Actor{
  var receive_events = false
  var is_running = true
  val service = new NIOService()
  var socket: NIOSocket = null
  val recv_loop = 
    new Thread(new Runnable
               {
                 override def run ={
                   Console.println("loop started")
                   while(is_running){
                     if(receive_events){                     
                       service.selectNonBlocking
                       Thread.sleep(50)
                     }
                   }
                   Console.println("loop stopped")
                 }  
                })
 
  def connect(address: String, port: Integer) ={
    socket = service.openSocket(address, port.intValue)
    
    try{
      socket.setPacketReader(new DelimiterPacketReader(13));

	  socket.listen(new SocketObserver() {
	    def connectionOpened(nioSocket: NIOSocket) {
  	      applicationClient ! Connected
  	    }

	    def packetReceived(socket: NIOSocket, packet: Array[byte]) {
  	      try {
	        val response = new String(packet).trim()
		    Console.println("S: " + response)
            applicationClient ! ReceivedDataMessage(response)
   	      } 
          catch {
		    case e: Exception => e.printStackTrace();
 		  }
	    }

	    def connectionBroken(nioSocket: NIOSocket,	e: Exception) {
	      System.out.println("Connection failed: " + e.getStackTraceString)
		  disconnect
	    }
	  });
    } 
    catch  {
      case e:Exception => e.printStackTrace()
    }
    
    receive_events = true
    Console.println("Network service connected")
  }
  
  override def start ={
     recv_loop.start
     super.start
  }
  
  def disconnect{
    if(socket != null)
      socket.close
    receive_events = false
    Console.println("Network service disconnected")
  }
  
  def act ={
    while (true) {
      receive{
        case Stop =>
          is_running = false
          Console.println("NagaNetworkService stopped")
          exit
        case Connect(address: String, port: Integer) =>
          connect(address, port)
        case Disconnect =>
          disconnect
        case SendDataMessage(tag, text) => 
          Console.println("C: " + tag + " " + text)
          socket.write((tag + " " + text + "\r\n").getBytes())
        case SendRawDataMessage(text) => 
          Console.println("C: " + text)
          socket.write((text + "\r\n").getBytes())
      }
	}
  }
}
