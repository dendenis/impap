package org.imap.client

import scala.actors.Actor
import scala.actors._
import org.imap.common.Item
import org.imap.state.AbstractState

import java.lang.Integer
import javax.mail.internet.MimeMessage

case class Connect(address: String, port: Integer)
case class Disconnect

case class ReceivedDataMessage(text: String)
case class Connected

case class ListFolder(folder: Item, client: Actor)

case class Start
case class Stop

case class SetState(state: AbstractState)
case class AddFolder(folder: String)
case class AddMessage(folder: Item, message: MimeMessage, uid: String)

case class SendDataMessage(tag: String, text: String)
case class SendRawDataMessage(text: String)
case class SendLastDataMessage(tag: String, text: String)

