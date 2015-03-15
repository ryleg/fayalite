scalaJSSettings

name := "fayalite-app-dynamic"

organization := "fayalite"

version := "0.0.3"

lazy val sparkVersion = "1.2.1"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.10.4"

net.virtualvoid.sbt.graph.Plugin.graphSettings

skip in ScalaJSKeys.packageJSDependencies := false

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/mavenrepo"

resolvers += "Spray" at "http://repo.spray.io"

resolvers += Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test" withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6" withSources() withJavadoc(),
  "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6" withSources() withJavadoc(),
  "com.lihaoyi" %% "upickle" % "0.2.6"
)

lazy val core = (project in file("."))