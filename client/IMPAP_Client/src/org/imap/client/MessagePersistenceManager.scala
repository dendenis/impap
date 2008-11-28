package org.imap.client

import scala.actors.Actor
import scala.actors.Actor._


import java.util.Properties
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import sun.misc.BASE64Encoder
import sun.misc.BASE64Decoder
import java.util.HashSet
import javax.mail.internet.MimeMessage
import javax.mail.Session
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import org.imap.message._
import org.imap.common.CompositeLogger


class MessagePersistenceManager(logger: CompositeLogger, imapClient: Actor) extends Actor {
  val mailboxFileName = "mailbox"
  val itemIdSet = new HashSet[String]()
  val messageIdSet = new HashSet[String]()
  
  def readMailBox(reader: BufferedReader): Unit ={
    val line = reader.readLine
    if(line != null){
      val array = line.split(":")
      val dec = new BASE64Decoder()
      
      
      if(array(0).equals("FolderItem")){
        val fullPath = new String(dec.decodeBuffer(array(1)))
        itemIdSet.add(fullPath)
        imapClient ! AddFolderItem(fullPath)
      }
      else if(array(0).equals("MessageItem")){
        val id = new String(dec.decodeBuffer(array(1)))
        val folderName = new String(dec.decodeBuffer(array(2)))
        val fullPath = new String(dec.decodeBuffer(array(3)))
        val content = new String(dec.decodeBuffer(array(4)))
        
        itemIdSet.add(id)
        
        val stream = new ByteArrayInputStream(content.getBytes)
        try{
          val message = new MimeMessage(Session.getDefaultInstance(new Properties()), stream)
          val folder = new FolderItem(folderName, fullPath)
          imapClient ! AddMessageItem(folder, message, id)
        }
        finally{
          stream.close
        }
      }
      else if(array(0).equals("BasicMessage")){
        val id = new String(dec.decodeBuffer(array(1)))
        val content = new String(dec.decodeBuffer(array(2)))
        val flags = new String(dec.decodeBuffer(array(3)))
        
        messageIdSet.add(id)
        
        val stream = new ByteArrayInputStream(content.getBytes)
        try{
          val mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()), stream)
          val message = new BasicMessage(mimeMessage, flags.split(" ", 0), id)
          imapClient ! AddMessage(message)
        }
        finally{
          stream.close
        }
      }
      readMailBox(reader)
    }
  }
  
  def saveFolder(folder: String) ={
    if(!itemIdSet.contains(folder)){
      try{
        val stream = new FileOutputStream(mailboxFileName, true)
        try{
          val enc = new BASE64Encoder()
          val line = "FolderItem:" + enc.encode(folder.getBytes()) + "\n"
          stream.write(line.getBytes)
        }
        finally{
          stream.close
        }
        itemIdSet.add(folder)
      }
      catch{
        case e: Exception => 
          logger.info("failed to save folder: " + e.getMessage)
          e.printStackTrace
      }
    }  
  }
  
  def saveMessageItem(folder: Item, message: MimeMessage, id: String) ={
    if(!itemIdSet.contains(id)){
      try{
        val stream = new FileOutputStream(mailboxFileName, true)
        try{
          val enc = new BASE64Encoder()
          val byteStream = new ByteArrayOutputStream()
          message.writeTo(byteStream)
          val content = enc.encode(byteStream.toByteArray).replaceAll("\r", "").replaceAll("\n", "")
          byteStream.close
          val line = "MessageItem:" + enc.encode(id.getBytes) + ":" + enc.encode(folder.getId.getBytes) +
                     ":" +  enc.encode(folder.getText.getBytes) + ":" + content + "\n"
          stream.write(line.getBytes)
        }
        finally{
          stream.close
        }
        itemIdSet.add(id)
        System.err.println("closed")
      }
      catch{
        case e: Exception => 
          logger.info("failed to save message item: " + e.getMessage)
          e.printStackTrace
      }
    }  
  }
  
   def saveMessage(message: Message) ={
    if(!messageIdSet.contains(message.getId)){
      try{
        val stream = new FileOutputStream(mailboxFileName, true)
        try{
          stream.write(message.serialize.getBytes)
        }
        finally{
          stream.close
        }
        messageIdSet.add(message.getId)
      }
      catch{
        case e: Exception => 
          logger.info("failed to save basic message: " + e.getMessage)
          e.printStackTrace
      }
    }  
  }
  
  def act={
    loop{
      react{
        case Start =>
          try{
            val file = new File(mailboxFileName)
            if(!file.exists){
              file.createNewFile
            }
          
            val stream = new FileInputStream(mailboxFileName)
            val reader = new BufferedReader(new InputStreamReader(stream))
            try{
              readMailBox(reader)
            }
            finally{
              reader.close
            }
          }
          catch{
            case e: Exception => 
              logger.info("Failed to read mailbox: " + e.getMessage)
              e.printStackTrace
          }
        case AddFolderItem(folder) =>
          saveFolder(folder)
        case AddMessageItem(folder, message, messageUid) =>
          saveMessageItem(folder, message, messageUid)
        case AddMessage(message) =>
          saveMessage(message)
        case Stop => exit
      }
    }
  }
}
