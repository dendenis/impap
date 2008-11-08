package org.imap.state

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.message.Item
import java.lang.Integer
import java.util.LinkedList

class ListFolderState(client: Actor, tag: Integer, folder: Item) extends AbstractState(client, tag){
  val uidList = new LinkedList[String]
  
  override def reaction(msg: Any) ={
    msg match {
      case Start =>
        client ! SendDataMessage("" + tag, "UID FETCH 1:* (UID)");
      case msg: Any => super.reaction(msg)  
    }
  }
  
  override def receivedDataRegex: String = "\\* [0-9]+ FETCH \\(UID ([0-9]+)\\)"
  
  override def onPatternMatch(matcher: Matcher) ={
    uidList.add(matcher.group(1))
  }
  
  override def onOK ={
    val array = uidList.toArray( new Array[String](0))
    setState(new ReceiveHeaderState(client, tag.intValue + 1, folder, array.toList))
  }
}