//import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)

scalaVersion := "2.11.6"

persistLauncher in Compile := true

persistLauncher in Test := false

skip in packageJSDependencies := false

testFrameworks += new TestFramework("utest.runner.Framework")

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "amateras-repo" at "http://amateras.sourceforge.jp/mvn/"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0" withSources() withJavadoc(),
  "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
  "com.lihaoyi" %%% "scalarx" % "0.2.8" withSources() withJavadoc(),
  "com.lihaoyi" %%% "upickle" % "0.2.8" withSources() withJavadoc(),
  "com.lihaoyi" %%% "scalatags" % "0.5.2" withSources() withJavadoc() //,
  //"com.scalawarrior" %%% "scalajs-ace" % "0.0.3" withSources() withJavadoc()
 // "be.doeraene" %%% "scalajs-jquery" % "0.8.0"
)

jsDependencies += "org.webjars" % "ace" % "1.2.3" / "src/ace.js"

/*
jsDependencies += "org.webjars.bower" % "moment" % "2.10.6" / "moment.js"

jsDependencies += "org.webjars.bower" % "zeroclipboard" % "2.2.0" / "zeroclipboard.js"

jsDependencies += "org.webjars.bower" % "handsontable" % "0.17.0" / "handsontable.js"
*/



//bootSnippet := "org.fayalite.ui.app.DynamicEntryApp().main();"

//updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)
