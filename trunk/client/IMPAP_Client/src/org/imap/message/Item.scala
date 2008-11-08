package org.imap.message

import java.util.TreeMap
import java.util.Map
import java.lang.Boolean

trait Item {
  val children = new TreeMap[String, Item]

  def getText: String
  def getId: String
  def getChildren: Map[String, Item] = children
  def isFolder: Boolean
    
  override def toString = getText + " " + children
}
