package org.imap.message

import javax.mail.internet.MimeMessage
import javax.mail.Flags
import java.lang.Boolean
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.HashSet

class BasicMessage (message: MimeMessage, flags: Array[String], id: String) extends Message{
  val flagsSet = new HashSet[String]()
  
  override def isDownloaded: Boolean = true
  override def subject: String = message.getSubject
  override def from: String = message.getFrom.foldLeft("")(_ +  _ + " ").trim
  override def to: String = message.getAllRecipients.foldLeft("")(_ +  _ + " ").trim
  override def content: String ={
    Console.println("before BasicMessage getContent")
    if(message.getContent.isInstanceOf[InputStream]){
      val stream = message.getContent.asInstanceOf[InputStream]
      val reader = new BufferedReader(new InputStreamReader(stream))
      var text = reader.readLine
      while(reader.ready){
        text = text + reader.readLine
      }
      Console.println("after BasicMessage getContent (InputStream)")
      text
    }else{
      Console.println("after BasicMessage getContent (String)")
      message.getContent.toString
    }
  }
  override def getId: String = id
  override def getFlag(flag: String): Boolean ={
    if(flagsSet.isEmpty()){
      for(f: String <- flags){
        flagsSet.add(f)
      }
    }
    flagsSet.contains(flag)
    
  }
  
  override def setFlag(flag: String, value: Boolean)={
    if(value.booleanValue){
      if(!flagsSet.contains(flag)){
        flagsSet.add(flag)
      }
    }
    else{
      if(flagsSet.contains(flag)){
        flagsSet.remove(flag)
      }
    }
    
    Console.println("New Flags " + flagsSet.toString)
  }
  
}
