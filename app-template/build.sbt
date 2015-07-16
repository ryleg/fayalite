//import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)

//workbenchSettings

name := "fayalite-app-template"

organization := "fayalite"

version := "0.0.4"

scalaVersion := "2.11.6"

persistLauncher in Compile := true

persistLauncher in Test := false

skip in packageJSDependencies := false

testFrameworks += new TestFramework("utest.runner.Framework")

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0" withSources() withJavadoc(),
  "com.lihaoyi" %%% "scalarx" % "0.2.8" withSources() withJavadoc(),
  "com.lihaoyi" %%% "upickle" % "0.2.8" withSources() withJavadoc(),
  "com.lihaoyi" %%% "scalatags" % "0.5.2" withSources() withJavadoc()
)

//bootSnippet := "org.fayalite.ui.app.DynamicEntryApp().main();"

//updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)
