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

class DeleteFolderState(client: Actor, tag: Integer, logger: CompositeLogger, folder: Item) extends AbstractState(client, tag, logger){

  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        client ! SendDataMessage("" + tag, "DELETE \"" + folder.getId + "\"");
      case msg: Any => super.reaction(msg)  
    }
  }
  
  override def onOK ={
    setState(new GetFoldersState(client, tag.intValue + 1, logger))
  }
  override def onNO ={
    setState(new GetFoldersState(client, tag.intValue + 1, logger))
  }
}