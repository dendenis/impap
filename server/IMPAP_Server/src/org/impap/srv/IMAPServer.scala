package org.impap.srv

import scala.actors.Actor
import scala.actors.Actor._
import java.util.regex.Pattern
import java.util.regex.Matcher
import java.nio.channels.SocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class IMAPServer(channel: SocketChannel) extends Actor {
  var sendService = new SendService(channel)
  var receiveService = new ReceiveService(this, channel)
  
  def act() {
    loop
    {
      react
      {
        case Connected =>
          Console.println("connected")
          sendService.start
          receiveService.start
          sendService ! SendDataMessage(IMAPConstants.ASTERISK_TAG, IMAPConstants.OK_RESULT + " "  + IMAPConstants.CONNECTED_MESSAGE)
          receiveService ! ReceiveCommand
          
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
          sendService ! Stop
          receiveService ! Stop

          if(channel.isConnected)
          {
            channel.close
          }
      }
    }
  }
 
   private def proc_capability(tag: String) {
      sendService ! SendDataMessage(IMAPConstants.ASTERISK_TAG, "IMAP4rev1 AUTH=PLAIN")
      sendService ! SendDataMessage(tag,  IMAPConstants.OK_RESULT + " " + IMAPConstants.CAPABILITY_COMMAND + " completed")
    }
   
  private def proc_logout(tag: String){
      sendService ! SendDataMessage(IMAPConstants.ASTERISK_TAG, "BYE IMAP4rev1 Server logging out")
      sendService ! SendLastDataMessage(tag, IMAPConstants.OK_RESULT + " " +  IMAPConstants.LOGOUT_COMMAND + " completed")
  }

  private def proc_unrecognized(tag: String){
      val response = IMAPConstants.BAD_RESULT + " " +  IMAPConstants.UNRECOGNIZED_COMMAND
      sendService ! SendDataMessage(tag, response)
  }

  private def proc_missing(tag: String) {
    val response =IMAPConstants.BAD_RESULT + " " + IMAPConstants.MISSING_COMMAND
    sendService ! SendDataMessage(IMAPConstants.ASTERISK_TAG, response)
  }
}