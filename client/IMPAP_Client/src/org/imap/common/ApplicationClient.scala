package org.imap.common

import java.lang.Boolean
import java.lang.Integer
import org.imap.message.Item
import org.imap.message.Message

trait ApplicationClient {
    var changed = true
	def getItemTree: Item
    def getMessage(folder: Item, id: String): Message
	def isChanged: Boolean = changed
    def setChanged(changed: Boolean) = this.changed = changed.booleanValue
    def listFolder(folder: Item)
    def addLogger(logger: Logger)
    def close
    def connect(address: String, port: Integer, username: String, pass: String)
    def disconnect
    def saveMessage(to: String, from: String, subject: String, content: String, folder: Item)
    def setFlag(folder: Item, message: Message, flag: String, value: Boolean)
}
