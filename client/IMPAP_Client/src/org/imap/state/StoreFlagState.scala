package org.imap.state

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.client._
import org.imap.common.CompositeLogger
import org.imap.message.Item
import java.lang.Integer
import java.lang.Boolean

class StoreFlagState(client: Actor, tag: Integer, logger: CompositeLogger, uid: String, flag: String, value: Boolean) extends AbstractState(client, tag, logger){

  override def reaction(msg: Any) ={
    msg match {
        case Start =>
          val stringValue = if(value.booleanValue) "+" else "-"
          client ! SendDataMessage("" + tag, "UID STORE " + uid + " " + stringValue + "FLAGS" + "(" + flag + ")")
        case msg: Any => super.reaction(msg)  
    }
  }
  
  override def onOK ={
    setState(new IdleState(client, tag.intValue + 1, logger))
  }
}