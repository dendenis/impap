package org.imap.state

import scala.actors.Actor
import scala.actors.Actor._
import org.imap.client._

import java.util.regex.Matcher;
import java.lang.Integer
import sun.misc.BASE64Encoder;

class AuthenticatingState(client: Actor, tag: Integer, username: String, pass: String) extends AbstractState(client, tag){
  
  override def reaction(msg: Any) ={
    msg match {
        case Connected =>
          Console.println("Connected!")          
          client ! SendDataMessage("" + tag, "AUTHENTICATE PLAIN")
          Console.println("Sent")          
        case msg: Any => super.reaction(msg)    
    }
  }
  
  override def receivedDataRegex: String = "\\+$"
  
  override def onOK ={
    setState(new GetFoldersState(client, tag.intValue + 1))
  }

  override def onPatternMatch(matcher: Matcher) ={
    val enc = new BASE64Encoder()
    sender ! SendRawDataMessage(enc.encode(("\0" + username + "\0" + pass).getBytes))
  }
}
