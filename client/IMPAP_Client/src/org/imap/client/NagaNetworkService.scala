package org.imap.client

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.common.CompositeLogger

import naga.ConnectionAcceptor
import naga.NIOServerSocket
import naga.NIOService
import naga.NIOSocket
import naga.ServerSocketObserverAdapter
import naga.SocketObserver
import naga.packetreader.DelimiterPacketReader
import java.lang.Integer

class NagaNetworkService(applicationClient: Actor, logger: CompositeLogger) extends Actor{
  var receive_events = false
  var is_running = true
  val service = new NIOService()
  var socket: NIOSocket = null
  val recv_loop = 
    new Thread(new Runnable
               {
                 override def run ={
                   logger.debug("loop started")
                   while(is_running){
                     if(receive_events){    
                       service.selectNonBlocking
                       Thread.sleep(50)
                     }
                   }
                   logger.debug("loop stopped")
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
		    logger.info("S: " + response)
            applicationClient ! ReceivedDataMessage(response)
   	      } 
          catch {
		    case e: Exception => e.printStackTrace();
 		  }
	    }

	    def connectionBroken(nioSocket: NIOSocket,	e: Exception) {
	      logger.info("Connection failed: " + e.getStackTraceString)
		  applicationClient ! Disconnect
	    }
	  });
    } 
    catch  {
      case e:Exception => e.printStackTrace()
    }
    
    receive_events = true
    logger.debug("Network service connected")
  }
  
  override def start ={
     recv_loop.start
     super.start
  }
  
  def disconnect{
    if(socket != null)
      socket.close
    receive_events = false
    logger.debug("Network service disconnected")
  }
  
  def act ={
    while (true) {
      receive{
        case Stop =>
          is_running = false
          logger.debug("NagaNetworkService stopped")
          exit
        case Connect(address: String, port: Integer) =>
          connect(address, port)
        case Disconnect =>
          disconnect
        case SendDataMessage(tag, text) => 
          logger.info("C: " + tag + " " + text)
          socket.write((tag + " " + text + "\r\n").getBytes())
        case SendRawDataMessage(text) => 
          logger.info("C: " + text)
          socket.write((text + "\r\n").getBytes())
      }
	}
  }
}
