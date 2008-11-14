package org.imap.state

import scala.actors.Actor
import scala.actors.Actor._
import org.imap.client._
import org.imap.common.CompositeLogger

import java.util.regex.Matcher;
import java.lang.Integer
import sun.misc.BASE64Encoder;

class AuthenticatingState(client: Actor, tag: Integer, logger: CompositeLogger, username: String, pass: String) extends AbstractState(client, tag, logger){
  
  override def reaction(msg: Any) ={
    msg match {
        case Connected =>
          logger.debug("Connected!")          
          client ! SendDataMessage("" + tag, "AUTHENTICATE PLAIN")
          logger.debug("Sent")          
        case msg: Any => super.reaction(msg)    
    }
  }
  
  override def receivedDataRegex: String = "\\+$"
  
  override def onOK ={
    setState(new GetFoldersState(client, tag.intValue + 1, logger))
  }

  override def onPatternMatch(matcher: Matcher) ={
    val enc = new BASE64Encoder()
    sender ! SendRawDataMessage(enc.encode(("\0" + username + "\0" + pass).getBytes))
  }
}
