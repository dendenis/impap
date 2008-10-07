package org.impap.srv

import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.io.IOException
import java.net.InetSocketAddress;
import java.lang.Integer
import java.util.HashSet

class TCPServer(serverFactory: ServerFactory, port: Integer){
  val channel = ServerSocketChannel.open()
  val serverSocket = channel.socket()
  val selector = Selector.open()
  val clients = new HashSet[SocketChannel]()
  
  def start = {
    val address = new InetSocketAddress(port.intValue)
  	serverSocket.bind(address)
	channel.configureBlocking(false)
	channel.register(selector, SelectionKey.OP_ACCEPT)
 
    System.out.println("server " + address.getHostName + ":" + address.getPort + " started");
    var i = 0;
 
    while(true) { 
      try {
        i = i + 1
        selector.select(50)
        if(i % 20 == 0){
          Console.println("Number of clients:" + clients.size)
        }
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
        clients.add(clientSocket.getChannel)
        clientSocket.setSoTimeout(100)
        val applicationServer = serverFactory.create(clientSocket.getChannel)
        applicationServer.start
        applicationServer ! Connected
      }
    }
  }
}
