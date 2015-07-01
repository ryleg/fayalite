import AssemblyKeys._ // put this at the top of the file

assemblySettings

parallelExecution in Test := false

jarName in assembly := "fayalite.jar"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case q if q.contains(".DS_Store") => MergeStrategy.discard
  case x => old(x)
}
}

test in assembly := {}

excludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar"}
}

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
