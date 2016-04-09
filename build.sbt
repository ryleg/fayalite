enablePlugins(ScalaJSPlugin)

persistLauncher in Compile := true

persistLauncher in Test := false

skip in packageJSDependencies := false


//testFrameworks += new TestFramework("utest.runner.Framework")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0" withSources() withJavadoc()
 // "com.lihaoyi" %%% "scalatags" % "0.5.4" withSources() withJavadoc(),
 //"com.lihaoyi" %%% "scalarx" % "0.2.8" withSources() withJavadoc()
)


/* "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
 "com.lihaoyi" %%% "upickle" % "0.2.8" withSources() withJavadoc(),
 "com.lihaoyi" %%% "scalatags" % "0.5.2" withSources() withJavadoc() //,
)

*/
mainClass in (Compile, run) := Some("org.fayalite.sjs.App")
