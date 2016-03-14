package org.fayalite.agg

import java.io.File

import fa._
import org.fayalite.agg.MailTester.EmailGuessRequirements
import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.RemoteWebDriver
import rx.core.Var


object PJSMailTester {
  def apply() = {

  }

}

class PJSMailTester(var webDriver: RemoteWebDriver, val id: Int = 0) extends MailTesterLike {

  val pjD = this

  def restartDriver() = {
    val cap = webDriver.getCapabilities
    T{webDriver.close()}
    webDriver = new PhantomJSDriver(cap)
    webDriver.get("http://www.mailtester.com")
  }

  val numEmailsTested = Var(0)

  def processLineActual(
                         line: List[String],
                         egr:EmailGuessRequirements
                       ) = {
    //println("Processing line " + line + " with EGR " + egr)
    val preEm = egr.preExistingEmail
    val perm = MailTester.getPermutations(egr.name)
    val firstPerm = perm.head
    val firstEmailToTest = preEm.getOrElse(firstPerm + "@" + egr.domain)

    //println("Testing first email " + firstEmailToTest)
    val (clr) = pjD.testEmail(firstEmailToTest)
    numEmailsTested() += 1
    println("First " + firstEmailToTest + " " + clr)

    import MailTester._

    val rep = pjD.webDriver.getPageSource

    val acceptsConnections = !rep.contains("Mailserver does not accept connections")
    val isDomainValid = !rep.contains(invalidMailDomain)
    val canMakeVerificationTests = !rep.contains("Server doesn't allow e-mail address verification")

    println("isDomainValid " + isDomainValid + " " + egr.domain)
    println("canMakeVerificationTests " + canMakeVerificationTests + " " + egr.domain)

    var reps = List((clr, firstEmailToTest))

    if (isDomainValid && canMakeVerificationTests && acceptsConnections) {
      perm.tail.map {
        qq =>
          val q = qq + "@" + egr.domain
      //      println("Waiting before testing next email 10s")
            Thread.sleep(6000)
         //   println("Testing permutation email : " + q)
            val (cl) = pjD.testEmail(q)
          numEmailsTested() += 1
          println(q + " " + cl)
       //     println("Waiting before testing next email 10s")
            Thread.sleep(6000)
            reps :+= (cl, q)
          }
    }

    val testedEmails = reps
    val longestGreenOpt = reps
      .filter{_._1 == "Green"}.map{_._2}.sortBy{_.length}.reverse.headOption

    val debugLine =  List(
      isDomainValid.toString,
      canMakeVerificationTests.toString,
      acceptsConnections.toString
    )
    line ++ debugLine ++ Seq(
      longestGreenOpt.getOrElse(""), reps.filter{
        q => q._1 != "Red" && !longestGreenOpt.contains(q._1) && !preEm.contains(q._1)
      }.map{_._2}.mkString(" ")
    )
  }


}
//domainAllowsVerification
trait MailTesterLike {

  import MailTester._

  var webDriver : RemoteWebDriver

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
     // println("submitEmailTestRequest " + e)
      submitEmailTestRequest(e)
      getEmailColor
  }

  def testEmailDbg(e: String) = {
    submitEmailTestRequest(e)
    getEmailColor  -> getReport
  }

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
