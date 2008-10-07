package org.impap.srv

object IMAPConstants {
  val COMMAND_PATTERN = "([0-9a-zA-Z]+) ([0-9a-zA-Z]+)(.*)";
  val CAPABILITY_COMMAND = "CAPABILITY";
  val LOGOUT_COMMAND = "LOGOUT";
  val UNRECOGNIZED_COMMAND = "unrecognized command";
  val MISSING_COMMAND = "Missing command";
  val CONNECTED_MESSAGE = "127.0.0.1 server ready";
  
  val OK_RESULT = "OK"
  val NO_RESULT = "NO"
  val BAD_RESULT = "BAD"
  
  val ASTERISK_TAG = "*"
  val PLUS_TAG = "+"
  
  val EOL: String = 10.toChar.toString + 13.toChar.toString
}
