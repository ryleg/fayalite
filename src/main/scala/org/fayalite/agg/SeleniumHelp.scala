package org.fayalite.agg

/**
  * Created by aa on 3/3/2016.
  */
trait SeleniumHelp {

  import fa._

  /**
    * Workaround requiring a packaged install with same values in
    * directory or in working directory of IntelliJ git repo
    * @return : Path to driver executable relative
    */
  def getDriverPath = {
    val winDriver = "chromedriver.exe"
    val macDriver = "chromedriver-mac32"
    val driver = if (osName.toLowerCase.contains("win")) winDriver
    else macDriver
    driver
  }

  /**
    * Selenium / ChromeDriver runs a secondary process that proxies
    * communications back, this must reflect an updated binary
    * version of the chromedriver executable release
    * @return
    */
  def setDriverProperty() = {
    System.setProperty("webdriver.chrome.driver", getDriverPath)
  }

}
