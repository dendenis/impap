package org.imap.state

import scala.actors.Actor
import scala.actors.Actor._
import org.imap.client._

import java.lang.Integer
import java.util.regex.Matcher;
import java.util.regex.Pattern;


abstract class AbstractState(client: Actor, tag: Integer) extends Actor{
  override def start = {
    Console.println(this.getClass + " started")
    super.start
  }
  
  override def exit = {
    Console.println(this.getClass + " stopped")
    super.exit
  }
  
  def act{
    loop{
      react{
        case msg: Any => reaction(msg)
      }        
    }
  }
  
  def reaction(msg: Any) ={
    msg match {
        case ReceivedDataMessage(message) =>
          if (message.startsWith("" + tag + " OK")) {
            onOK
   	      }
          else if (message.startsWith("" + tag + " NO")) {
            onNO
   	      }
          else if (message.startsWith("" + tag + " BAD")) {
            onBAD
   	      }
          else if(receivedDataRegex != null){
              val pattern = Pattern.compile(receivedDataRegex);
	          val matcher = pattern.matcher(message);
  		  
              if (matcher.find()) {
                onPatternMatch(matcher)
              }
              else{
                onText(message)
              }
          }
          else{
            onText(message)
          }
          
        case Disconnect =>
  		  setState(new DisconnectedState(client, tag.intValue + 1))
        case Stop =>
          exit
        case msg: Any =>
          Console.println("Unknown message: " + msg.toString)
    }
  }
  
  def setState(state: AbstractState) ={
    client ! SetState(state)
  }
  
  def getTag: Integer = tag
  
  def onOK ={
    setState(new IdleState(client, tag.intValue + 1))
  }
  
  def onNO ={
    setState(new IdleState(client, tag.intValue + 1))
  }
  
  def onBAD ={
    setState(new IdleState(client, tag.intValue + 1))
  }
  
  def receivedDataRegex: String = null
  
  def onPatternMatch(matcher: Matcher) = {}
  
  def onText(message: String) = {}
}
