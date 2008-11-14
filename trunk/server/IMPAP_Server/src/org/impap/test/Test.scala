package org.impap.test

import java.lang.Character

object Test {
  val EOL: String = 10.toChar.toString + 13.toChar.toString
  
  def main(args : Array[String]) : Unit = 
    {
      Console.print("q" + EOL + "q")
    }
}
