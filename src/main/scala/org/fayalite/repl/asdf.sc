import scala.tools.nsc.Settings

val settings = new Settings
val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation

println("compilerPath=" + compilerPath)
println("settings.bootclasspath.value=" + settings.bootclasspath.value)

settings.bootclasspath.value = List(settings.bootclasspath.value, compilerPath, libPath) mkString java.io.File.pathSeparator
settings.usejavacp.value = true