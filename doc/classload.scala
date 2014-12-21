
import java.io.File
import java.net.URL
import org.apache.spark.SparkConf
import org.apache.spark.executor.{ExecutorURLClassLoader, ChildExecutorURLClassLoader, MutableURLClassLoader}
import org.apache.spark.util.Utils
val currentLoader = Thread.currentThread().getContextClassLoader
val urls = Array(new
val parent : MutableURLClassLoader = new ExecutorURLClassLoader(urls, currentLoader)
val classUri = $intp.classServer.uri
val conf = new SparkConf(true)
val userCP : java.lang.Boolean = false
val klass = Class.forName("org.apache.spark.repl.ExecutorClassLoader").asInstanceOf[Class[_ <: ClassLoader]]
val constructor = klass.getConstructor(classOf[SparkConf], classOf[String],
  classOf[ClassLoader], classOf[Boolean])
val cl = constructor.newInstance(conf, classUri, parent, userCP)
import org.apache.spark.SparkContext
import org.apache.spark.SparkEnv

val icl = $intp.classLoader

val cname = "Asdf"
val classToLoad = Class.forName (cname, true, icl)




val classToLoad = Class.forName (cname, false, cl)


val classToLoad = Class.forName (cname, false, cl)

classToLoad.getDeclaredMethods
val mname ="HEADER_DELIM"
val method = classToLoad.getDeclaredMethod (mname)

val instance = classToLoad.newInstance()
val result = method.invoke (null)


currentLoader
val classToLoad = Class.forName ("Asdf", false, cl)




import scala.util.{Try, Success, Failure}
//   implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.runtime.{universe => ru, currentMirror => m}
def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]

val theType = getTypeTag(res10).tpe
//val m = ru.runtimeMirror(getClass.getClassLoader)

val m = ru.runtimeMirror(cl)


import scala.tools.reflect.ToolBox

val tb = m.mkToolBox()

val tree = tb.parse("1 to 3 map (_+1)")

val eval = tb.eval(tree)



val f = classOf[ClassLoader].getDeclaredField("classes")
f.setAccessible(true);

f.get(cl)

Vector<Class> classes =  (Vector<Class>) f.get(classLoader);
reboot


val reflections = new Reflections("org.apache.spark");

Set<Class<? extends Object>> allClasses =
  reflections.getSubTypesOf(Object.class);


object C { def x = 2 }

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}
val m = ru.runtimeMirror(getClass.getClassLoader)

val objectC = typeOf[C.type].termSymbol.asModule

typeOf[interp.memberHandlers.intp.global.TermName]

val mm = m.reflectModule(objectC)

val obj = mm.instance
