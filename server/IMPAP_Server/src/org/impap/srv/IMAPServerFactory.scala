package org.impap.srv

import java.nio.channels.SocketChannel;
import scala.actors.Actor

class IMAPServerFactory extends ServerFactory{
  def create(channel: SocketChannel): Actor = return new IMAPServer(channel) 
}
