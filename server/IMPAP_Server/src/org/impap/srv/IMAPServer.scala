package org.impap.srv

import scala.actors.Actor
import scala.actors.Actor._
import java.util.regex.Pattern
import java.util.regex.Matcher
import java.io.BufferedReader;
import java.io.InputStreamReader;
import naga.NIOSocket;

class IMAPServer(socket: NIOSocket) extends Actor {
//  var sendService = new SendService(channel)
//  var receiveService = new ReceiveService(this, channel)
  
  def sendData(tag: String, text: String) = {
    socket.write((tag + " " + text + IMAPConstants.EOL).getBytes()) 
  }                                      

  def sendLastData(tag: String, text: String) = {
    socket.write((tag + " " + text + IMAPConstants.EOL).getBytes())
    socket.closeAfterWrite
  }                                      

  def act() {
    loop
    {
      react
      {
        case Connected =>
          Console.println("connected")
  //        sendService.start
//          receiveService.start
          sendData(IMAPConstants.ASTERISK_TAG, IMAPConstants.OK_RESULT + " "  + IMAPConstants.CONNECTED_MESSAGE)
//          sendService ! SendDataMessage(IMAPConstants.ASTERISK_TAG, IMAPConstants.OK_RESULT + " "  + IMAPConstants.CONNECTED_MESSAGE)
//          receiveService ! ReceiveCommand
          
        case ReceivedDataMessage(text) =>
       //   Console.println("C: " + text)
          val pattern = Pattern.compile(IMAPConstants.COMMAND_PATTERN)
          val matcher = pattern.matcher(text);
        
          if(matcher.find())
          {
            val tag = matcher.group(1);
            val command = matcher.group(2).toUpperCase();
            val args = matcher.group(3);
            this ! IMAPCommand(tag, command, args)
          }
          else
          {
            this ! IMAPCommand(IMAPConstants.ASTERISK_TAG, IMAPConstants.MISSING_COMMAND, "")  
          }
          
        case IMAPCommand(tag, IMAPConstants.CAPABILITY_COMMAND, args) =>
          proc_capability(tag)

        case IMAPCommand(tag, IMAPConstants.LOGOUT_COMMAND, args) =>
          proc_logout(tag)

        case IMAPCommand(tag, IMAPConstants.MISSING_COMMAND, args) =>
          proc_missing(tag)
          
        case IMAPCommand(tag, unrecognizedCommand, args) =>
          proc_unrecognized(tag)
          
        case Stop =>
          Console.println("Disconnecting")
//          sendService ! Stop
//          receiveService ! Stop
          socket.close

      }
    }
  }
 
   private def proc_capability(tag: String) {
     sendData(IMAPConstants.ASTERISK_TAG, IMAPConstants.CAPABILITY_COMMAND + " IMAP4rev1 AUTH=PLAIN")
     sendData(tag,  IMAPConstants.OK_RESULT + " " + IMAPConstants.CAPABILITY_COMMAND + " completed")
   }
   
  private def proc_logout(tag: String){
      sendData(IMAPConstants.ASTERISK_TAG, "BYE IMAP4rev1 Server logging out")
      sendLastData(tag, IMAPConstants.OK_RESULT + " " +  IMAPConstants.LOGOUT_COMMAND + " completed")
  }

  private def proc_unrecognized(tag: String){
      val response = IMAPConstants.BAD_RESULT + " " +  IMAPConstants.UNRECOGNIZED_COMMAND
      sendData(tag, response)
  }

  private def proc_missing(tag: String) {
    val response =IMAPConstants.BAD_RESULT + " " + IMAPConstants.MISSING_COMMAND
    sendData(IMAPConstants.ASTERISK_TAG, response)
  }
}
