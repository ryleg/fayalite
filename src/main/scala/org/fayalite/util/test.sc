
import dispatch._



def ge(s: String) = {
  val svc = url(s).GET
  Http(svc OK as.String)
}

val sf = "https://sfdata.gov"
