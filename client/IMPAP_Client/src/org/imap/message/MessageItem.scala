package org.imap.message

import javax.mail.internet.MimeMessage
import java.lang.Boolean
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class MessageItem(message: MimeMessage, id: String) extends Item{
  
  override def getText: String ={
     from + " " + message.getSubject
  }
  
  override def getId: String = id
  override def isFolder: Boolean = false

  def subject: String = message.getSubject
  def from: String = message.getFrom.foldLeft("")(_ +  _ + " ").trim
}
