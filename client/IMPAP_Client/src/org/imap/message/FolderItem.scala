package org.imap.message

import java.lang.Boolean

class FolderItem(name: String, fullPath: String) extends Item {
  override def getText: String = name
  override def getId: String = fullPath
  override def isFolder: Boolean = true
}
