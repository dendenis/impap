package org.imap.message

import java.lang.Boolean

class NotDownloadedMessage(id: String) extends Message{
  override def isDownloaded: Boolean = false
  override def subject: String = ""
  override def from: String = ""
  override def to: String = ""
  override def content: String = ""
  override def getId: String = id
}
