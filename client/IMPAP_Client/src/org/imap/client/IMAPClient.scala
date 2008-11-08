package org.imap.client

import scala.actors.Actor
import scala.actors.Actor._

import org.imap.message.Item;
import org.imap.message._;

import org.imap.state._
import org.imap.common.ApplicationClient

import com.sun.mail.dsn.MessageHeaders

import java.io.ByteArrayInputStream
import java.lang.Boolean
import java.lang.Integer
import java.util.HashMap

class IMAPClient extends Actor with ApplicationClient{
  var state: AbstractState = new DisconnectedState(this, 0)
  val root = new FolderItem("root", "/")
  val messages = new HashMap[String, Message] 
  var networkService: Actor = null
  
  def start(networkService: Actor) = {
    this.networkService = networkService
    state.start
    super.start
  }
  
  def connect(address: String, port: Integer, username: String, pass: String) ={
    val oldTag = state.getTag
    state ! Stop
    state = new AuthenticatingState(this, oldTag.intValue + 1, username, pass)
    state.start
    networkService ! Connect(address, port)
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
        case ReceivedDataMessage(text) =>
          state ! ReceivedDataMessage(text)
        case SendDataMessage(tag, text) =>
          networkService ! SendDataMessage(tag, text)
        case SendRawDataMessage(text) =>
          networkService ! SendRawDataMessage(text)
        case SendLastDataMessage(tag, text) =>
          networkService ! SendLastDataMessage(tag, text)
        case SetState(newState) =>
          Console.println("Got set state event")
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
          Console.println("IMAPClient stopped")
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
  
  def close ={
    this ! Stop
    state ! Stop
    networkService ! Stop
  }
}
