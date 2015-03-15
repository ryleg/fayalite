import AssemblyKeys._ // put this at the top of the file

assemblySettings

parallelExecution in Test := false

jarName in assembly := "app-dynamic.jar"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case q if q.contains(".DS_Store") => MergeStrategy.discard
  case PathList("org", "apache", "http", "impl", xs @ _*) => MergeStrategy.first
  case x => old(x)
}
}

test in assembly := {}

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
