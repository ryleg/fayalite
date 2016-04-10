import com.sun.javadoc._
import scala.collection.JavaConversions
import JavaConversions._
class ListParams extends Doclet {

  def start( root : RootDoc) {
    val classes = root.classes()
    classes.foreach { cd =>
      printMembers(cd.constructors().toArray)
      printMembers(cd.methods().toArray)
    }
  }

 def printMembers(mems: Array[ExecutableMemberDoc]) {
    mems.foreach{m =>
      val params = m.paramTags();
      println(m.qualifiedName());
     params.foreach{p =>
        System.out.println("   " + p.parameterName()
          + " - " + p.parameterComment());
      }
    }
  }
}
val l = new ListParams()

l.