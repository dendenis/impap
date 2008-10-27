package org.imap.common

import javax.mail.internet.MimeMessage
import java.lang.Boolean

class MessageItem(message: MimeMessage, id: String) extends Item("message", id){
  
  override def getName: String ={
     from + " " + message.getSubject
  }

  override def subject: String = message.getSubject
  override def from: String = message.getFrom.foldLeft("")(_ +  _ + " ").trim
  override def to: String = message.getAllRecipients.foldLeft("")(_ +  _ + " ").trim
  override def text: String = message.getContent.toString
  
  override def isMessage: Boolean = true
  override def isFolder: Boolean = false
  override def toString = getName
}
