package org.imap.message

import java.lang.Boolean

trait Message {
  def isDownloaded: Boolean
  def subject: String
  def from: String
  def to: String
  def content: String
  def getId: String
  def getFlag(flag: String): Boolean
  def setFlag(flag: String, value: Boolean)
}
