package org.imap.client

import scala.actors.Actor
import scala.actors._
import org.imap.message.Item
import org.imap.message.Message
import org.imap.state.AbstractState

import java.lang.Integer
import javax.mail.internet.MimeMessage

case class Connect(address: String, port: Integer)
case class Disconnect

case class ReceivedDataMessage(text: String)
case class Connected

case class ListFolder(folder: Item, client: Actor)
case class GetMessage(folder: Item, uid: String)

case class Start
case class Stop

case class SetState(state: AbstractState)
case class AddFolderItem(folder: String)
case class AddMessageItem(folder: Item, message: MimeMessage, uid: String)
case class AddMessage(message: Message)

case class SendDataMessage(tag: String, text: String)
case class SendRawDataMessage(text: String)
case class SendLastDataMessage(tag: String, text: String)

