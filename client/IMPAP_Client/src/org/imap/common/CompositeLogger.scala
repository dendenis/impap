package org.imap.common

import scala.collection.mutable.LinkedList;

class CompositeLogger {
  val debugLoggers = new LinkedList[String => Unit]((str: String) => Console.println(str), null)
  val infoLoggers = new LinkedList[String => Unit]((str: String) => Console.println(str), null)

  def debug(str: String) = debugLoggers.foreach(logger => logger(str))
  def info(str: String) = infoLoggers.foreach(logger => logger(str))
  def addDebug(logger: Logger) = 
    debugLoggers.append(new LinkedList[String => Unit]((str: String) => logger.log(str), null))
  def addInfo(logger: Logger) = 
    infoLoggers.append(new LinkedList[String => Unit]((str: String) => logger.log(str), null))
}
