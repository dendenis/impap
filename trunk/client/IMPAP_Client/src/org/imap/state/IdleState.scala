package org.imap.state

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.common._
import java.lang.Integer

class IdleState(client: Actor, tag: Integer) extends AbstractState(client, tag){
  
  override def reaction(msg: Any) ={
    msg match {
       case ListFolder(folder, client) =>
          Console.println("about to list folder " + folder)
          setState(new SelectState(client, tag.intValue + 1, folder))
       case GetMessage(folder, uid) =>
          setState(new ReceiveMessageState(client, tag.intValue + 1, folder, uid))

       case msg: Any => super.reaction(msg)  
    }
  }
}