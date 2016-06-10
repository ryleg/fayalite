Modified Spark Distribution (required for spark submodule)
https://s3-us-west-1.amazonaws.com/fayalite/spark.tar.gz

(For use with IntelliJ, put the assembly jar in /lib/)

See http://github.com/ryleg/spark for building from source

Still experimental. Working on Spark 2.0.0 snapshot REPL integrations

Run :

See gate submodule - org.fayalite.gate.server.SprayServer <- Run this locally
and navigate to http://localhost:8080 to see Scala.js app

Use sbt ~fastOptJS from main project directory to recompile SJS app
Refresh page to reload app

Motivations : https://github.com/ryleg/fayalite/blob/master/doc/Motivations.md
