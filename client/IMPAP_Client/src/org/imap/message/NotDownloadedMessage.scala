package org.imap.message

import java.lang.Boolean

class NotDownloadedMessage(id: String) extends Message{
  override def isDownloaded: Boolean = false
  override def subject: String = ""
  override def from: String = ""
  override def to: String = ""
  override def content: String = ""
  override def getId: String = id
  override def getFlag(flag: String): Boolean = false
  override def setFlag(flag: String, value: Boolean) = {}
  override def serialize: String = {throw new Exception("Not supported")}
}
