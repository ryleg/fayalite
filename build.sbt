name := "fayalite"

organization := "fayalite"

version := "0.0.3"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.10.4"

resolvers ++= Seq("mvnrepository" at "http://mvnrepository.com/artifact/")

resolvers ++= Seq("Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Spray" at "http://repo.spray.io"

resolvers += Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns)

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.7.2"  withSources() withJavadoc(),
  "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.25.0" % "test",
  "amplab" % "spark-indexedrdd" % "0.1",
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
  "com.lihaoyi" %% "scalarx" % "0.2.7" withSources() withJavadoc(),
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "com.lihaoyi" %% "ammonite-ops" % "0.2.7" withSources() withJavadoc(),
  "org.scalaz" %% "scalaz-core" % "7.1.1" withSources() withJavadoc(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.7"  withSources() withJavadoc(),
   "com.googlecode.lanterna" % "lanterna" % "2.1.9",
  "com.jcraft" % "jzlib" % "1.1.3",
  "com.decodified" %% "scala-ssh" % "0.7.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.github.tototoshi" %% "scala-csv" % "1.2.1" withSources() withJavadoc(),
  //"com.healthmarketscience.jackcess" % "jackcess" % "2.1.2",
  "com.github.sarxos" % "webcam-capture" % "0.3.10" withSources() withJavadoc()
)