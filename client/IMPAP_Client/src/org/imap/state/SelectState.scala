package org.imap.state

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.common._
import java.lang.Integer

class SelectState(client: Actor, tag: Integer, folder: Item) extends AbstractState(client, tag){

  override def reaction(msg: Any) ={
    msg match {
        case Start =>
          client ! SendDataMessage("" + tag, "SELECT \"" + folder.getName + "\"");
        case msg: Any => super.reaction(msg)  
    }
  }
  
  override def onOK ={
    setState(new ListFolderState(client, tag.intValue + 1, folder))
  }
}