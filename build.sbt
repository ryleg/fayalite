scalaJSSettings

name := "fayalite"

organization := "fayalite"

version := "0.0.3"

lazy val sparkVersion = "1.2.1"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.10.4"

net.virtualvoid.sbt.graph.Plugin.graphSettings

skip in ScalaJSKeys.packageJSDependencies := true

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/mavenrepo"

resolvers += "Spray" at "http://repo.spray.io"

resolvers += Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

mainClass in (Compile, run) := Some("org.fayalite.ui.SimpleServer")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test" withSources() withJavadoc(),
  "org.json4s" %% "json4s-core" % "3.2.10" % "provided" withSources() withJavadoc(),
  "org.json4s" %% "json4s-jackson" % "3.2.10" % "provided" withSources() withJavadoc(),
  "org.json4s" %% "json4s-ext" % "3.2.10" withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.codehaus.jackson" % "jackson-core-asl" % "1.8.8" % "provided" withSources(),
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.8" % "provided" withSources(),
  "com.github.fge" % "jackson-coreutils" % "1.8" withSources(),
  "com.amazonaws" % "aws-java-sdk" % "1.8.9.1" withSources(),
  "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4" withSources() withJavadoc(),
  "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6" withSources() withJavadoc(),
  "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6" withSources() withJavadoc(),
  "com.lihaoyi" %% "scalarx" % "0.2.7" withSources() withJavadoc(),
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
  "com.lihaoyi" %% "ammonite-ops" % "0.2.7" withSources() withJavadoc(),
  "org.scalaz" %% "scalaz-core" % "7.1.1" withSources() withJavadoc(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.7"  withSources() withJavadoc()
)