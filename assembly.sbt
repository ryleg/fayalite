import AssemblyKeys._ // put this at the top of the file

assemblySettings

Project.inConfig(Test)(baseAssemblySettings)

parallelExecution in Test := false

jarName in (Test, assembly) := s"${name.value}-test-${version.value}.jar"

mergeStrategy in (Test, assembly) <<= (mergeStrategy in assembly) { (old) =>
{
  case q if q.contains(".DS_Store") ||
    q.contains("JS_DEPENDENCIES") ||
    q.contains("Bootstrap.class") ||
    q.contains("netty") ||
    q.contains("META-INF/BCKEY.DSA") ||
    q.contains("META-INF") ||
    q.contains("META-INF/BCKEY.SF") =>
    MergeStrategy.discard
  case x => old(x)
}
}

//assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)

//run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
