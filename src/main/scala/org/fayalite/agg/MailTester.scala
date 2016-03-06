package org.fayalite.agg

import java.io.File

import fa._


class MailTester(toTest: Seq[String])
  extends SeleniumChrome(Some("http://mailtester.com")) {

  import MailTester._

  val forceStarted = started.get.get

  def getEmailInputBox = webDriver
    .findElementByXPath(emailInputXPath)

  def submit() = webDriver
    .findElementByClassName("Button").click()

  def submitEmailTestRequest(email: String) = {
    val e = getEmailInputBox
    e.clear()
    e.sendKeys(email)
    submit()
  }

  def getEmailColorCode = webDriver
    .findElementByXPath(colorXPath)
    .getAttribute("bgcolor")

  def getEmailColor = {
    val c = getEmailColorCode
    if (c == redEmailBgColor) "Red" else
    if (c == yellowEmailBgColor) "Yellow" else "Green"
  }
}

/**
  * Created by aa on 3/5/2016.
  */
object MailTester {

  val addressMotFoundOnServer = "E-mail address does not exist on this server"
  val invalidMailDomain = "Invalid mail domain."
  val emailInputXPath =  "//*[@id=\"content\"]/form/table/tbody/tr[1]/td/input"
  val colorXPath = "//*[@id=\"content\"]/table/tbody/tr[1]/td[1]"
  val yellowEmailBgColor = "#FFBB00"
  val redEmailBgColor = "#FF4444"

  case class Name(first: String, last: String)

  case class EmailGuessRequirements(name: Name, domain: String)

  def processFile(f: File) = {
    val csv = readCSV(f.getAbsolutePath)
    val withLowercase = csv.tail.map{_.map{_.toLowerCase}}
    val headers = csv.head
    (headers, withLowercase)
  }

  def getPermutations(n: Name) = {
    val f = n.first
    val l = n.last
    List(
      f,
      l,
      f(0).toString + l,
      f(0).toString + "." + l,
      f + l,
      f + "." + l,
      f + "." + l(0),
      f(0) + l(0)
    )
  }

  def processLine(q: Seq[String]) = {
    val frs = q(0)
    val lst = q(1)
    val dmn = q(2)
    val u = dmn.withOut(
      List("http://", "https://", "www\\.", "/")
    )
    val uu = u match {
      case z if z.contains("\\.") => z.split("\\.").tail.mkString
      case z => z
    }
   EmailGuessRequirements(Name(frs, lst), uu)
  }

/*
    val newCSV = headers :: updatedRows
    writeCSV(".output", newCSV)
*/

}
