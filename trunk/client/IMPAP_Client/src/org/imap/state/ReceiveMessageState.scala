package org.imap.state

import java.util.regex.Matcher;
import javax.mail.internet.MimeMessage
import javax.mail.Session
import java.util.Properties
import java.io.ByteArrayInputStream

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.common._
import java.lang.Integer

class ReceiveMessageState(client: Actor, tag: Integer, folder: Item, messageUids: List[String]) extends AbstractState(client, tag){
  val uid = if(!messageUids.isEmpty) messageUids.first else null
  val result = new StringBuilder

  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        if(uid == null){
          setState(new IdleState(client, tag.intValue + 1))
        }
        else{
          client ! SendDataMessage("" + tag, "UID FETCH " + uid + " (BODY[])");
        }     
      case msg: Any => super.reaction(msg)  
    }
  }
  
  override def receivedDataRegex: String = "(\\*.*" + uid + ".*)|(\\)$)"
  
  override def onText(message: String) ={
    result.append(message).append("\n")
  }
  
  override def onOK ={
    Console.println("msg = \n" + result)
    Console.println("msg end")
    val stream = new ByteArrayInputStream(result.toString.trim.getBytes)
    try{
      val message = new MimeMessage(Session.getDefaultInstance(new Properties()), stream)
      client ! AddMessage(folder, message, uid)
    }
    finally{
      stream.close
      setState(new ReceiveMessageState(client, tag.intValue + 1, folder, messageUids.tail))
    }
  }
}