package org.impap.srv

import java.nio.channels.SocketChannel;
import scala.actors.Actor

trait ServerFactory {
  def create(channel: SocketChannel): Actor
}
