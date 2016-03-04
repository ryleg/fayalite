package org.fayalite.layer

/**
  * For all message passing case classes and other
  * inter-application communication protocols.
  */
object Schema {

  /**
    * For Selenium / phantomJS / JS Cookies, expressed
    * as extracted by Scalatest Selenium in order
    * @param name : Key for identifying cookie
    * @param domain : Applicable domain cookie loads upon
    * @param path : Path
    * @param value : Contents of cookies
    * @param expiry : Date encoded string
    */
  case class Cookie(
                     name: String,
                     domain: String,
                     path: String,
                     value: String,
                     expiry: String
                   )


  /**
    * For short user inputs that require
    * understanding / breaking down into words
    * or sanitizing in some way
    * @param clean : Modified version of the user input
    * @param original : The original version
    * @param words : The cleaned version's interpreted split of words
    *              in given String phrase.
    */
  case class FixedPhrase(
                        clean: String,
                        original: String,
                        words: List[String]
                      )

}
