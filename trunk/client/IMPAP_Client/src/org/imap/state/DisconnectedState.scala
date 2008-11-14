package org.imap.state

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.Future
import org.imap.client._
import org.imap.common.CompositeLogger
import java.lang.Integer

class DisconnectedState(client: Actor, tag: Integer, logger: CompositeLogger) extends AbstractState(client, tag, logger){
  
  override def reaction(msg: Any) ={
    msg match {
        case Authenticate(username, pass) =>
  		  setState(new AuthenticatingState(client, tag.intValue + 1, logger, username, pass))
        case msg: Any => super.reaction(msg)  
    }
  }
 
}
