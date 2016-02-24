import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import sbt.dsl._


object FayaliteBuild extends sbt.Build {

  addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.2")

  override lazy val settings = super.settings ++
    Seq(
      resolvers := Seq(

        "mvnrepository" at "http://mvnrepository.com/artifact/",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        "Spray" at "http://repo.spray.io",
        Resolver.sonatypeRepo("snapshots"),
        Resolver.url("scala-js-releases",
          url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
          Resolver.ivyStylePatterns),
        "Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local",
        "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven",
        Resolver.jcenterRepo,
        "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
      ),
      licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
    )

  enablePlugins(ScalaJSPlugin)


  val jsonStuff = Seq(
    "org.json4s" %% "json4s-core" % "3.2.10" % "provided" withSources() withJavadoc(),
    "org.json4s" %% "json4s-jackson" % "3.2.10" % "provided" withSources() withJavadoc(),
    "org.codehaus.jackson" % "jackson-core-asl" % "1.8.8" % "provided" withSources(),
    "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.8" % "provided" withSources(),
    "org.json4s" %% "json4s-ext" % "3.2.10" withSources() withJavadoc(),
    "com.github.fge" % "jackson-coreutils" % "1.8" withSources()
  )

  val misc = Seq(
    //"com.healthmarketscience.jackcess" % "jackcess" % "2.1.2",
    "com.github.sarxos" % "webcam-capture" % "0.3.10" withSources() withJavadoc()
  )

  val essential = Seq(
    "com.lihaoyi" %% "scalarx" % "0.2.7" withSources() withJavadoc(),
    "com.lihaoyi" %% "ammonite-ops" % "0.2.7" withSources() withJavadoc(),
    "org.scalaz" %% "scalaz-core" % "7.1.1" withSources() withJavadoc()
  )

  val web = Seq(
    "org.jsoup" % "jsoup" % "1.7.2"  withSources() withJavadoc(),
    "org.scalacheck" %% "scalacheck" % "1.11.4" % "test" withSources() withJavadoc(),
    "org.seleniumhq.selenium" % "selenium-java" % "2.25.0" % "test",
    "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4" withSources() withJavadoc(),
    "com.amazonaws" % "aws-java-sdk" % "1.8.9.1" withSources()
  )

  lazy val root = Project(
    id = "root",
    base = file(".")).settings(
    name := "fayalite",
    organization := "fayalite",
    version := "0.0.3",
    scalaVersion := "2.10.4",
    libraryDependencies :=
      jsonStuff ++
        essential ++
        web ++
        misc ++
        Seq(
          "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test",
          "amplab" % "spark-indexedrdd" % "0.1",
          "com.beachape.filemanagement" %% "schwatcher" % "0.1.7"  withSources() withJavadoc(),
          "com.googlecode.lanterna" % "lanterna" % "2.1.9",
          "com.jcraft" % "jzlib" % "1.1.3",
          "com.decodified" %% "scala-ssh" % "0.7.0",
          "ch.qos.logback" % "logback-classic" % "1.1.2",
          "com.github.tototoshi" %% "scala-csv" % "1.2.1" withSources() withJavadoc()
        )
  )

  lazy val sjs = Project(
    id = "sjs",
    base = file("./sjs")
  )

  mainClass in (Compile, run) := Some("org.fayalite.sjs.App")

}
