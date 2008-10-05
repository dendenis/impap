package org.impap.srv

import java.nio.channels.SocketChannel;

case class ReceivedDataMessage(text: String)
case class Connected

case class SendDataMessage(text: String)
case class SendLastDataMessage(text: String)
case class ReceiveCommand
case class Stop

case class IMAPCommand(tag: String, name: String, args: String)
case class MissingCommand
