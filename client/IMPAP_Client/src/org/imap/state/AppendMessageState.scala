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

class AppendMessageState(client: Actor, tag: Integer, logger: CompositeLogger, folder: Item, message: String, date: String) extends AbstractState(client, tag, logger){

  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        client ! SendDataMessage("" + tag, "APPEND \"" + folder.getId + "\" (\\Seen) " + date + " {" + message.length() + "+}");
        client ! SendRawDataMessage(message)
      case msg: Any => super.reaction(msg)  
    }
  }
  
  override def onOK ={
      setState(new SelectState(client, tag.intValue + 1, logger, folder))
  }
  
  override def receivedDataRegex: String = "APPEND Failed"
  
  override def onPatternMatch(matcher: Matcher) ={
    setState(new IdleState(client, tag.intValue + 1, logger))
  }
}