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

class ReceiveFlagsState(client: Actor, tag: Integer, logger: CompositeLogger, uid: String, mimeMessage: MimeMessage) extends AbstractState(client, tag, logger){
  val result = new StringBuilder

  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        client ! SendDataMessage("" + tag, "UID FETCH " + uid + " (FLAGS)");
      case msg: Any => super.reaction(msg)  
    }
  }
  
//  * 1 FETCH (UID 19878828 FLAGS (\Seen XAOL-READ XAOL-GOODCHECK-DONE XAOL-BILLPAY-MAIL))
  override def receivedDataRegex: String = ".*FLAGS \\((.*)\\)\\)$"
  
  override def onPatternMatch(matcher: Matcher) ={
    val flags = matcher.group(1)
    logger.debug("received flags " + flags)
    val message = new BasicMessage(mimeMessage, flags.split(" ", 0), uid)
    client ! AddMessage(message)
  }
  
  override def onOK ={
    setState(new IdleState(client, tag.intValue + 1, logger))
  }
}