package org.imap.message

import javax.mail.internet.MimeMessage
import java.lang.Boolean
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class BasicMessage (message: MimeMessage, id: String) extends Message{
  override def isDownloaded: Boolean = true
  override def subject: String = message.getSubject
  override def from: String = message.getFrom.foldLeft("")(_ +  _ + " ").trim
  override def to: String = message.getAllRecipients.foldLeft("")(_ +  _ + " ").trim
  override def content: String ={
    Console.println("MessageItem getContent")
    if(message.getContent.isInstanceOf[InputStream]){
      val stream = message.getContent.asInstanceOf[InputStream]
      val reader = new BufferedReader(new InputStreamReader(stream))
      var text = reader.readLine
      while(reader.ready){
        text = text + reader.readLine
      }
      text
    }else{
      message.getContent.toString
    }
  }
  override def getId: String = id
}
