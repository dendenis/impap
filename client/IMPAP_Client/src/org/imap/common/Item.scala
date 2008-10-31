package org.imap.common

import java.util.TreeMap
import java.util.Map
import java.lang.Boolean

class Item(name: String, id: String) {
  val children = new TreeMap[String, Item]

  def getName: String = name
  def getChildren: Map[String, Item] = children
  def isMessage: Boolean = false
  def isFolder: Boolean = true
    
  def subject: String = ""
  def from: String = ""
  def to: String = ""
  def content: String = {
    Console.println("Item getContent")
    ""
  }
  def contentType: String = "text/html"
  
  override def toString = name + " " + children
}
