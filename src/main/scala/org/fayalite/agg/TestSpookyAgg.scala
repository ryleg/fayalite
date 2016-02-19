/*
package org.fayalite.agg

import org.tribbloid.spookystuff.SpookyContext
import org.tribbloid.spookystuff.example.ExampleCore
import org.tribbloid.spookystuff.actions._
import org.tribbloid.spookystuff.dsl._
import JobBoard.byPage
/*

//remember infix operator cannot be written in new line
object LinkedInS extends ExampleCore {


  override def doMain(spooky: SpookyContext) = {

    val t = byPage(2)
    val q: Visit = Visit(t)
    spooky.fetch(
        Visit("http://www.utexas.edu/world/univ/alpha/")
      )
      .flatten($"div.box2 a".text ~ 'name, maxOrdinal = 10)
      .repartition(10)
      .fetch(
        Visit("http://images.google.com/")
          +> WaitFor("form[action=\"/search\"]")
          +> TextInput("input[name=\"q\"]","Logo '{name}")
          +> Submit("input[name=\"btnG\"]")
          +> WaitFor("div#search")
      )
      .wgetJoin($"div#search img".src, maxOrdinal = 1)
      .savePages(
        x"file://${System.getProperty("user.home")}/spooky/$appName/${'name}"
      )

  }
}
*/

object JobBoard {


  def byPage(page: Int) = {
    s"http://www.seek.com.au/jobs-in-information-communication-technology" +
      s"/in-australia/#dateRange=999&workType=0&industry=6281&" +
      s"occupation=&graduateSearch=false&salaryFrom=80000&" +
      s"salaryTo=999999&salaryType=annual&advertiserID=&" +
      s"advertiserGroup=&keywords=&page=$page&displaySuburb=&" +
      s"seoSuburb=&isAreaUnspecified=false&location=&area=&" +
      s"nation=3000&sortMode=ListedDate&searchFrom=quick&" +
      s"searchType="
  }

  def main(args: Array[String]) {

    import org.tribbloid.spookystuff.SpookyContext
    import org.tribbloid.spookystuff.actions._
    import org.tribbloid.spookystuff.dsl._
    import org.tribbloid.spookystuff.example.social.LinkedIn
    LinkedIn.main(Array())
    /*
         /**
          * A more complex linkedIn job that finds name and printout skills of all Sanjay Gupta in your local area
          */
         //remember infix operator cannot be written in new line
         object LinkedIn extends QueryCore {

           override def doMain(spooky: SpookyContext) = {
             import spooky.dsl._

             //    spooky.proxy = TorProxyFactory

             sc.parallelize(Seq("Sanjay", "Arun", "Hardik"))
               .fetch(
                 Visit("https://www.linkedin.com/")
                   +> TextInput("input#first", '_)
                   *> (TextInput("input#last", "Gupta") :: TextInput("input#last", "Krishnamurthy") :: Nil)
                   +> Submit("input[name=\"search\"]")
               )
               .visitJoin($"ol#result-set h2 a")
               .select(
                 $"span.full-name".text ~ 'name,
                 $"p.title".text ~ 'title,
                 $"div#profile-skills li".texts.mkString("|") ~ 'skills,
                 $.uri ~ 'uri
               )
               .toDataFrame()
           }
         }*/
    /*     val urlBoard = byPage(2)
         import dispatch._

         val svc = url(urlBoard)
         val country = Http(svc OK as.String)

         country.foreach{
           q =>
             println("response: " + q)
             println("url used ")
             println(urlBoard)
         }*/

  }

}
*/
