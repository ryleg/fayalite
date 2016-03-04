/**
  * Created by aa on 3/3/2016.
  */


/*    // Load the directory as a resource
    val dir_url =  ClassLoader.getSystemResource("org\\fayalite")
    val dir = new File(dir_url.toURI());
    // List the directory
    val  files = dir.list().toList
    println("files " + files)*/
/*

def indeedRemoteJobsUrl(page: Int) = {
 "http://www.indeed.com/jobs?q=&l=Remote&start=" + page*10
}

 def tes = {
new SimpleChrome(
       cwd / 'secret / 'thelocal / 'run,
       (1 to 22).toList.map{i =>
         "http://www.thelocal.se/jobs/?job_keyword=&job_category=engineer&job_category=it&page=" +
           i.toString}
//     "a"
     ).runBlocking()
 def parse(parsedExtr: ParsedExtr) = parsedExtr match {
   case ParsedExtr(url, q) =>
     q.sel("div.jobsitem").map {
       j =>
         Map("JobTitle" -> j.fsel("div.jobstitle").text,
           "JobLocation" -> j.fsel("div.jilocation").text,
           "JobDescription" -> Try{j.fsel("div.jisummary").text}.getOrElse("---MISSING---"),
           "JobSource" -> j.fsel("img.employer-logo").attr("alt"),
           "JobURL" -> j.parent().attr("href")
         ) ++
           Array("CompanyName", "JobDatePostedAsOfCrawl").zip(
             j.fsel("div.jicompany").text.split("\\|")).toMap
     }
 }
*/
/* readExtrParse(cwd / 'secret / 'thelocal / 'run, parse)
   .dedupe(getUniqueExistingCompanyNamesSanitized)
   .save((cwd / 'secret / 'thelocal / RelPath("new.csv")).toString())
*/
