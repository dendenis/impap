package org.imap.client

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.message.Item;
import org.imap.message._

import org.imap.state._
import org.imap.common.ApplicationClient
import org.imap.common.Logger
import org.imap.common.CompositeLogger

import com.sun.mail.dsn.MessageHeaders
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress

import javax.mail.Address
import javax.mail.Session
import javax.mail.Message.RecipientType
import java.util.Properties
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.io.ByteArrayOutputStream

import java.io.ByteArrayInputStream
import java.lang.Boolean
import java.lang.Integer
import java.util.HashMap
import java.util.HashSet
import scala.collection.mutable.LinkedList

class IMAPClient(logger: CompositeLogger) extends Actor with ApplicationClient{
  var state: AbstractState = new DisconnectedState(this, 0, logger)
  val root = new FolderItem("root", "/")
  val messages = new HashMap[String, Message] 
  var networkService: Actor = null
  
  def start(networkService: Actor) = {
    this.networkService = networkService
    state.start
    super.start
  }
  
  def addLogger(logger: Logger) = this.logger.addInfo(logger)
    
  def connect(address: String, port: Integer, username: String, pass: String) ={
    networkService ! Connect(address, port)
    state ! Authenticate(username, pass)
  }
    
  def disconnect ={
    networkService ! Disconnect
    state ! Disconnect
  }
  
  def listFolder(folder: Item) ={
    state ! ListFolder(folder, this)
  }
  
  def act={
    loop{
      react{
        case Connected =>
          state ! Connected
        case Disconnect =>
          networkService ! Disconnect
          state ! Disconnect
        case ReceivedDataMessage(text) =>
          state ! ReceivedDataMessage(text)
        case SendDataMessage(tag, text) =>
          networkService ! SendDataMessage(tag, text)
        case SendRawDataMessage(text) =>
          networkService ! SendRawDataMessage(text)
        case SendLastDataMessage(tag, text) =>
          networkService ! SendLastDataMessage(tag, text)
        case SetState(newState) =>
          logger.debug("Got set state event")
          state ! Stop
          state = newState
          newState.start
          newState ! Start
        case AddFolderItem(folderStr) =>
          Console.println(folderStr)
          val folders = folderStr.split("/")
          updateTree(root, folders.toList, "")
          Console.println(root)
        case AddMessageItem(folder, message, messageUid) =>
          Console.println(messageUid)
          if(!folder.getChildren.containsKey(messageUid)){
            folder.children.put(messageUid, new MessageItem(message, messageUid))
            setChanged(true)
            Console.println(root)
          }
       case AddMessage(message) =>
         messages.put(message.getId, message)

        case Stop =>
          logger.debug("IMAPClient stopped")
          exit
      }
    }
  }
  
  def updateTree(position: Item, folders:List[String], parent: String):Unit ={
    if(folders.isEmpty)
      return
    val folder = folders.first
    if(!position.getChildren.containsKey(folder))
    {
      position.getChildren.put(folder, new FolderItem(folder, parent + folder))
      setChanged(true)
    }
    val child = position.getChildren.get(folder)
    updateTree(child, folders.tail, parent + folder + "/")
  }
  
  def getItemTree(): Item = root
  
  def getMessage(folder: Item, uid: String): Message ={
    if(messages.containsKey(uid)){
      messages.get(uid)
    }
    else{
      state ! GetMessage(folder, uid) 
      new NotDownloadedMessage(uid)
    }
  }
  
  def saveMessage(to: String, from: String, subject: String, content: String, folder: Item) ={
    val mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()))
    mimeMessage.addRecipients(RecipientType.TO, to)
    val addr = new Array[Address](1)
    addr(0) = new InternetAddress(from)
    mimeMessage.addFrom(addr)
    mimeMessage.addHeader("Subject", subject)
    val format = new SimpleDateFormat("\"dd-MMM-yyyy HH:mm:ss Z\"", Locale.US)
    val date = format.format(new Date())
    mimeMessage.addHeader("Date", date)
    mimeMessage.setContent(content, "text/plain");
    val stream = new ByteArrayOutputStream()
    mimeMessage.writeTo(stream)
    this ! SetState(new AppendMessageState(this, state.getTag.intValue + 1, logger, folder, stream.toString, date))
  }
  
  def setFlag(folder: Item, message: Message, flag: String, value: Boolean) ={
    message.setFlag(flag, value)
    this ! SetState(new StoreFlagState(this, state.getTag.intValue + 1, logger, message.getId, flag, value))
  }
  
  def close ={
    this ! Stop
    state ! Stop
    networkService ! Stop
  }
}
