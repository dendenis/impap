package org.impap.srv

import naga.NIOSocket;
import scala.actors.Actor

trait ServerFactory {
  def create(socket: NIOSocket): Actor
}
