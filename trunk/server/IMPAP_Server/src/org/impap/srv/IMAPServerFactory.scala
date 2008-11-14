package org.impap.srv

import naga.NIOSocket
import scala.actors.Actor

class IMAPServerFactory extends ServerFactory{
  def create(socket: NIOSocket): Actor = return new IMAPServer(socket) 
}
