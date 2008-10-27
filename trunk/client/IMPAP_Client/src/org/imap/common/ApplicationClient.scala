package org.imap.common

import java.lang.Boolean
import java.lang.Integer

trait ApplicationClient {
    var changed = true
	def getRoot: Item
	def isChanged: Boolean = changed
    def setChanged(changed: Boolean) = this.changed = changed.booleanValue
    def listFolder(folder: Item)
    def close
    def connect(address: String, port: Integer, username: String, pass: String)
    def disconnect
}
