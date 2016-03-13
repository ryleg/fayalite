package org.fayalite.agg

import java.io.File

import fa._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.RemoteWebDriver


class PJSMailTester(val webDriver: RemoteWebDriver
                   ) extends MailTesterLike
//domainAllowsVerification
trait MailTesterLike {

  import MailTester._

  val webDriver : RemoteWebDriver

  def getEmailInputBox = webDriver
    .findElementByXPath(emailInputXPath)

  def getReport = {
    webDriver.findElementByXPath(fullDebugReportXPath).getText
  }

  def submitEmailInputQuery() = webDriver
    .findElementByClassName("Button").click()

  def submitEmailTestRequest(email: String) = {
    val e = getEmailInputBox
    e.clear()
    e.sendKeys(email)
    submitEmailInputQuery()
  }

  def getEmailColorCode = webDriver
    .findElementByXPath(colorXPath)
    .getAttribute("bgcolor")

  def getEmailColor = {
    val c = getEmailColorCode
    colorTransl.getOrElse(c, "MISSING BGCOLOR CODE")
  }

  def testEmail(e: String) = {
    submitEmailTestRequest(e)
    getEmailColor
  }

  def testEmailDbg(e: String) = {
    submitEmailTestRequest(e)
    getEmailColor -> getReport
  }

}

class MailTester
  extends SeleniumChrome(Some("http://mailtester.com"))
  with MailTesterLike
{


  val forceStarted = started.get.get

}

/**
  * Created by aa on 3/5/2016.
  */
object MailTester {

  val fullDebugReportXPath = "//*[@id=\"content\"]/table/tbody"
  val addressMotFoundOnServer = "E-mail address does not exist on this server"
  val invalidMailDomain = "Invalid mail domain."
  val emailInputXPath =  "//*[@id=\"content\"]/form/table/tbody/tr[1]/td/input"
  val colorXPath = "//*[@id=\"content\"]/table/tbody/tr[1]/td[1]"
  val yellowEmailBgColor = "#FFBB00"
  val redEmailBgColor = "#FF4444"
  val greenEmailBgColor = "#00DD00"
  val colorTransl = Map(
    "#FFBB00" -> "Yellow",
  "#FF4444" -> "Red",
  "#00DD00" -> "Green"
  )

  case class Name(first: String, last: String)

  case class EmailGuessRequirements(name: Name, domain: String,
                                    preExistingEmail: Option[String] = None)

  def processFile(f: File) = {
    val csv = readCSV(f.getAbsolutePath)
    val withLowercase = csv.tail.map{_.map{_.toLowerCase}}
    val headers = csv.head
    (headers, withLowercase)
  }

  def getPermutations(n: Name): List[String] = {
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
      f(0).toString + l(0)
    )
  }

  def processLine(q: Seq[String], firstIdx: Int, lastIdx: Int,
                  domainIdx: Int) = {
    val frs = q(firstIdx)
    val lst = q(lastIdx)
    val dmn = q(domainIdx)
    val u = dmn.withOut(
      List("http://", "https://", "www\\.", "/")
    )
    val uu = u match {
      case z if z.contains("\\.") => z.split("\\.").tail.mkString
      case z => z
    }
   EmailGuessRequirements(Name(frs, lst), uu)
  }

  def apply(path : String ) = {
    //processLine(new File(path))
  }

/*
    val newCSV = headers :: updatedRows
    writeCSV(".output", newCSV)
*/

}
