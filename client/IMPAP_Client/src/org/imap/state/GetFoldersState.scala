package org.imap.state

import java.util.regex.Matcher;

import scala.actors.Actor
import scala.actors.Actor._
import org.imap.common.CompositeLogger
import java.lang.Integer

import org.imap.client._

class GetFoldersState(client: Actor, tag: Integer, logger: CompositeLogger) extends AbstractState(client, tag, logger){

  override def reaction(msg: Any) ={
    msg match {
        case Start =>
          client ! SendDataMessage("" + tag, "LSUB \"\" \"*\"");
        case msg: Any => super.reaction(msg)  
    }
  }
  
  override def receivedDataRegex: String = "\\* LSUB \\([^\\(\\)]*\\) \"\\/\" ([A-Za-z0-9_/ ]+)"
  
  override def onPatternMatch(matcher: Matcher) ={
    client ! AddFolderItem(matcher.group(1))
  }
}
