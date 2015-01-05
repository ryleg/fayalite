import bintray.Keys._

name := "fayalite"

organization := "fayalite"

version := "0.0.21"

publishMavenStyle := true

Seq(bintraySettings:_*)

repository in bintray := "fayalite"

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

bintrayOrganization in bintray := None

scalaVersion := "2.10.4"

net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/mavenrepo"

libraryDependencies ++= Seq(
    "com.scalarx" %% "scalarx" % "0.2.6" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test" withSources() withJavadoc(),
  "org.apache.spark" %% "spark-core" % "1.1.0" % "provided" withSources() withJavadoc(),
  "org.apache.spark" %% "spark-mllib" % "1.1.0" % "provided" withSources() withJavadoc(),
  "org.apache.spark" %% "spark-repl" % "1.1.0" % "provided" withSources() withJavadoc(),
  "org.apache.hadoop" % "hadoop-client" % "1.0.4" % "provided",
  "org.json4s" %% "json4s-core" % "3.2.10" % "provided" withSources() withJavadoc(),
  "org.json4s" %% "json4s-jackson" % "3.2.10" % "provided" withSources() withJavadoc(),
  "org.json4s" %% "json4s-ext" % "3.2.10" withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.codehaus.jackson" % "jackson-core-asl" % "1.8.8" % "provided" withSources(),
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.8" % "provided" withSources(),
  "com.github.fge" % "jackson-coreutils" % "1.8" withSources(),
  "com.amazonaws" % "aws-java-sdk" % "1.8.9.1" withSources()
)
