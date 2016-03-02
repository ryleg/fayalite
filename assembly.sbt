import AssemblyKeys._ // put this at the top of the file

assemblySettings

parallelExecution in Test := false

jarName in assembly := "fayalite.jar"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case q if q.contains(".DS_Store") ||
    q.contains("JS_DEPENDENCIES") ||
    q.contains("META-INF/BCKEY.DSA") ||
    q.contains("META-INF/BCKEY.SF") =>
    MergeStrategy.discard
  case x => old(x)
}
}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)

test in assembly := {}

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
