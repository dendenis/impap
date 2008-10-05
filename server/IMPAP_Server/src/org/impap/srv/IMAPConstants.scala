package org.impap.srv

object IMAPConstants {
  val COMMAND_PATTERN = "([0-9a-zA-Z]+) ([0-9a-zA-Z]+)(.*)";
  val CAPABILITY_COMMAND = "CAPABILITY";
  val LOGOUT_COMMAND = "LOGOUT";
  val UNRECOGNIZED_COMMAND = "unrecognized command";
  val MISSING_COMMAND = "Missing command";
  val CONNECTED_MESSAGE = "* OK 127.0.0.1 server ready\n";
}
