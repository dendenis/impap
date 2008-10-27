package org.imap.state

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.Future
import org.imap.client._
import java.lang.Integer

class DisconnectedState(client: Actor, tag: Integer) extends AbstractState(client, tag){
  
  override def reaction(msg: Any) ={
    msg match {
//        case Connect(address, port) =>
//  		  setState(new AuthenticatingState(client, tag.intValue + 1, ))
        case msg: Any => super.reaction(msg)  
    }
  }
 
}
