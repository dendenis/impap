package org.impap.srv

object Main {
  def main(args : Array[String]) : Unit = 
  {
    val tcpServer = new TCPServer(new IMAPServerFactory, 143)
    tcpServer.start
  }
}
