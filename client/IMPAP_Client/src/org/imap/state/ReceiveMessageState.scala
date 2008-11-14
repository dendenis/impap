package org.imap.state

import java.util.regex.Matcher;
import javax.mail.internet.MimeMessage
import javax.mail.Session
import java.util.Properties
import java.io.ByteArrayInputStream

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.common.CompositeLogger
import org.imap.message.Item
import org.imap.message.BasicMessage
import java.lang.Integer

class ReceiveMessageState(client: Actor, tag: Integer, logger: CompositeLogger, folder: Item, uid: String) extends AbstractState(client, tag, logger){
  val result = new StringBuilder

  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        client ! SendDataMessage("" + tag, "UID FETCH " + uid + " (BODY[])");
      case msg: Any => super.reaction(msg)  
    }
  }
  
  override def receivedDataRegex: String = "(\\*.*" + uid + ".*)|(\\)$)"
  
  override def onText(message: String) ={
    result.append(message).append("\n")
  }
  
  override def onOK ={
    logger.debug("msg = \n" + result)
    logger.debug("msg end")
    val stream = new ByteArrayInputStream(result.toString.trim.getBytes)
    try{
      val mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), stream)
      val message = new BasicMessage(mimeMessage, uid)
      client ! AddMessage(message)
    }
    finally{
      stream.close
      setState(new IdleState(client, tag.intValue + 1, logger))
    }
  }
}