import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import sbt.dsl._


object FayaliteBuild extends sbt.Build {

  addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.2")
  bintray.BintrayKeys.bintrayVcsUrl := Some("git@github.com:ryleg/fayalite.git")

  val scalaV = "2.11.6"
  val fayaliteVersion = "0.0.5"

  val scalaDeps = Seq(
    "org.scala-lang"    %   "scala-compiler"      % scalaV withJavadoc() withSources(),
    "org.scala-lang"    %   "scala-library"       % scalaV withJavadoc() withSources(),
    "org.scala-lang"    %   "scala-reflect"       % scalaV withJavadoc() withSources()
  )

  parallelExecution in Test := false

  override lazy val settings = super.settings ++
    Seq(
      resolvers := Seq(
        "bintray" at "http://jcenter.bintray.com", // For bintray plugin stuff
        "softprops-maven" at "http://dl.bintray.com/content/softprops/maven",
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
        "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
        "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
        Resolver.bintrayRepo("ryleg", "maven")
      ),
      licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
    )

  enablePlugins(ScalaJSPlugin)


  val jsonStuff = Seq(
    "org.json4s" %% "json4s-core" % "3.2.10" withSources() withJavadoc(),
    "org.json4s" %% "json4s-jackson" % "3.2.10" withSources() withJavadoc(),
    "org.codehaus.jackson" % "jackson-core-asl" % "1.8.8" withSources(),
    "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.8" withSources(),
    "com.github.fge" % "jackson-coreutils" % "1.8" withSources()
  )

  //Unused // Should be for agg / parsers
  val msAccessDbParsing = "com.healthmarketscience.jackcess" % "jackcess" % "2.1.2"

  val auxParseLikeStuff = Seq(
    "com.github.tototoshi" %% "scala-csv" % "1.2.1" withSources() withJavadoc()
  )

  val rx = "com.lihaoyi" %% "scalarx" % "0.3.1" withSources() withJavadoc()

  val flair = Seq(
    rx,
    "com.lihaoyi" %% "ammonite-ops" % "0.2.7" withSources() withJavadoc(),
    "org.scalaz" %% "scalaz-core" % "7.1.1" withSources() withJavadoc()
  )

  val web = Seq(
    "org.jsoup" % "jsoup" % "1.7.2"  withSources() withJavadoc(),
    "org.seleniumhq.selenium" % "selenium-java" % "2.25.0" withSources() withJavadoc(),
    "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4" withSources() withJavadoc()
  )

  val sparkExperimental =   Seq(
    "amplab" % "spark-indexedrdd" % "0.1"
  )

  val testers = Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.6",
    "org.scalacheck" %% "scalacheck" % "1.11.4" % "test" withSources() withJavadoc()
  )

  val aggRelated = Seq(
    "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" withSources() withJavadoc()
  )


  val allDeps = jsonStuff ++
    flair ++
    web ++
    Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.14" withSources() withJavadoc()
      //     "ch.qos.logback" % "logback-classic" % "1.1.2",
    ) ++ aggRelated ++ testers ++ sparkExperimental ++ auxParseLikeStuff


  // For tests of BufferedImage at high speed
  val misc = Seq(
    "com.github.sarxos" % "webcam-capture" % "0.3.10" withSources() withJavadoc()
  )

  // Not used for primary codebase
  val opsLike = Seq(
    "com.beachape.filemanagement" %% "schwatcher" % "0.1.7"  withSources() withJavadoc(),
    "com.googlecode.lanterna" % "lanterna" % "2.1.9",
    "com.jcraft" % "jzlib" % "1.1.3",
    "com.decodified" %% "scala-ssh" % "0.7.0"
  )

  // Used for Scala.js, required to be
  // kept separate from other dependencies due to java conflicts
  // In theory SJS is supposed to be multi-module and handle this
  // Don't trust it.
  lazy val root = Project(
    id = "root",
    base = file(".")).settings(
    name := "fayalite",
    organization := "fayalite",
    version := fayaliteVersion,
    scalaVersion := "2.11.6"
  )

  lazy val common = Project(
    id = "common",
    base = file("./common")).settings(
    scalaVersion := "2.11.6",
    organization := "fayalite",
    version := fayaliteVersion
  )

  lazy val core = Project(
    id = "core",
    base = file("./core")).settings(
    scalaVersion := "2.11.6",
    organization := "fayalite",
    version := fayaliteVersion,
    libraryDependencies := allDeps ++ scalaDeps
  ) dependsOn common

  lazy val ml = Project(
    id = "ml",
    base = file("./ml")).settings(
    scalaVersion := "2.11.6",
    libraryDependencies := Seq(
      "xuggle" % "xuggle-xuggler" % "5.2", // For stitching images into video
      "com.twitter" % "algebird-core_2.11" % "0.12.0" withSources() withJavadoc(),
      "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.0",
      "com.sksamuel.scrimage" %% "scrimage-io-extra" % "2.1.0",
      "com.sksamuel.scrimage" %% "scrimage-filters" % "2.1.0",
      "org.scalanlp" %% "breeze" % "0.12",
      "org.scalanlp" %% "breeze-natives" % "0.12",
      "org.scalanlp" %% "breeze-viz" % "0.12",
      "org.apache.spark" %% "spark-core" % "1.6.1" withJavadoc() withSources()
    )
  ) dependsOn core

  lazy val agg = Project(
    id = "agg",
    base = file("./agg")).settings(
    scalaVersion := "2.11.6",
    organization := "fayalite",
    version := fayaliteVersion,
    libraryDependencies := Seq(
      "me.lessis" %% "courier" % "0.1.3" withSources() withJavadoc(),
      "com.github.mkroli" %% "dns4s-akka" % "0.9" withSources() withJavadoc()
    ) ++ scalaDeps
  ) dependsOn core


  lazy val gate = Project(
    id = "gate",
    base = file("./gate")).settings(
    scalaVersion := "2.11.6",
    libraryDependencies := web ++ Seq(
      rx,
      "com.lihaoyi" %% "scalatags" % "0.5.4" withSources() withJavadoc(),
      "com.amazonaws" % "aws-java-sdk" % "1.8.9.1" withSources()
    )
  ) dependsOn core

  lazy val spark = Project(
    id = "spark",
    base = file("./spark")).settings(
    scalaVersion := "2.11.6",
    libraryDependencies := Seq(
     // "SparkFlow" % "sparkflow_2.11" % "0.0.1"
    )
  )


  lazy val experimental = Project(
    id = "experimental",
    base = file("./experimental")).settings(
    scalaVersion := "2.11.6",
    libraryDependencies := Seq() ++ misc
  ) dependsOn core


  ivyScala := ivyScala.value map {_.copy(overrideScalaVersion = true)}

}
