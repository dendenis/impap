package org.imap.state

import java.util.regex.Matcher;

import scala.actors.Actor
import scala.actors.Actor._
import java.lang.Integer

import org.imap.client._

class GetFoldersState(client: Actor, tag: Integer) extends AbstractState(client, tag){

  override def reaction(msg: Any) ={
    msg match {
        case Start =>
          client ! SendDataMessage("" + tag, "LSUB \"\" \"*\"");
        case msg: Any => super.reaction(msg)  
    }
  }
  
  override def receivedDataRegex: String = "\\* LSUB \\([^\\(\\)]*\\) \"\\/\" ([A-Za-z0-9_/ ]+)"
  
  override def onPatternMatch(matcher: Matcher) ={
    client ! AddFolder(matcher.group(1))
  }
}
