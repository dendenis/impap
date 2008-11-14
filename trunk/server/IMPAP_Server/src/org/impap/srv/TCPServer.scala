package org.impap.srv

import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.io.IOException
import java.net.InetSocketAddress;
import java.lang.Integer
import java.util.HashSet

import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOService;
import naga.NIOSocket;
import naga.ServerSocketObserverAdapter;
import naga.SocketObserverAdapter;
import naga.packetreader.DelimiterPacketReader;

class TCPServer(serverFactory: ServerFactory, port: Integer){
  val channel = ServerSocketChannel.open()
  val serverSocket = channel.socket()
  val selector = Selector.open()
  val clients = new HashSet[SocketChannel]()
  
  def start = {
    try {
	  val service = new NIOService();
	  val socket = service.openServerSocket(port.intValue);

	 socket.listen(new ServerSocketObserverAdapter() {
	            var clientCount = 0
				override def newConnection(nioSocket: NIOSocket) =  {
					clientCount = clientCount + 1;
					Console.println("client count = " + clientCount);
					nioSocket.setPacketReader(new DelimiterPacketReader(
							 13));

					nioSocket.listen(new SocketObserverAdapter() {
                       val applicationServer = serverFactory.create(nioSocket)
                       applicationServer.start
                       applicationServer ! Connected

						override def packetReceived(socket: NIOSocket,
								packet: Array[byte]) = {
							applicationServer ! ReceivedDataMessage(new String(packet))
						}

						override def connectionBroken(nioSocket: NIOSocket,
								e: Exception) = {
						    //if(e != null){
						    //  Console.println("Connection broken: " + e.getMessage)
                            //}
							clientCount = clientCount - 1;
							Console.println("client count = " + clientCount);
						}
					});
				}

				override def acceptFailed(e: IOException) = {
					System.err.println("accept failed: " + e.getMessage());
				}
			});
	   
       socket.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
	     while (true) {
		   service.selectBlocking();
		 }
	   } catch {
	     case e: IOException =>	 e.printStackTrace();
	   }
  }
  
 //def check_for_events(){
//    var keys = selector.selectedKeys()
//    var key_iterator = keys.iterator()
//    while(key_iterator.hasNext()){
//      val key = key_iterator.next
//      key_iterator.remove()
//      if(key.isAcceptable())
//      {
// 	    val clientSocket = serverSocket.accept()
//        clients.add(clientSocket.getChannel)
//        clientSocket.setSoTimeout(100)
//        val applicationServer = serverFactory.create(clientSocket.getChannel)
//        applicationServer.start
//        applicationServer ! Connected
//      }
//    }
//  }
}
