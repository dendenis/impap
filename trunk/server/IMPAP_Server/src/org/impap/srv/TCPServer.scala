package org.impap.srv

import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.io.IOException
import java.net.InetSocketAddress;
import java.lang.Integer

class TCPServer(serverFactory: ServerFactory, port: Integer){
  val channel = ServerSocketChannel.open()
  val serverSocket = channel.socket()
  val selector = Selector.open()
  
  def start = {
    val address = new InetSocketAddress(port.intValue)
  	serverSocket.bind(address)
	channel.configureBlocking(false)
	channel.register(selector, SelectionKey.OP_ACCEPT)
 
    System.out.println("server " + address.getHostName + ":" + address.getPort + " started");
 
    while(true) { 
      try {
        selector.select(50)
      } catch { case e: IOException => println("IO Exception: " + e.getMessage ) }
      check_for_events
    }
  }
  
  def check_for_events(){
    var keys = selector.selectedKeys()
    var key_iterator = keys.iterator()
    while(key_iterator.hasNext()){
      val key = key_iterator.next
      key_iterator.remove()
      if(key.isAcceptable())
      {
 	    val clientSocket = serverSocket.accept()
        clientSocket.setSoTimeout(100)
        val applicationServer = serverFactory.create(clientSocket.getChannel)
        applicationServer.start
        applicationServer ! Connected
      }
    }
  }
}
