I'm testing a bunch of dynamic scala.js stuff right now. Highly unstable.
I put app (static) and app-dynamic in separate folders,

Need to switch out SparkSQL usage for postgres. SparkSQL is too unstable,
and the hive requirements for real use case in a web app are frustrating.

SparkSQL will work as a toy example but it's not scalable for serving an app

#Run

sbt ~fastOptJS

to compile dynamic-js page

cd fayalite/app-dynamic

sbt ~fastOptJS

Run org.fayalite.ui.ParseServer to handle state management / synchronization
Run org.fayalite.ui.WSServer to handle websocket management / serving page

Open http://localhost:8080 in browser


#Notes


to compile static-js page

cd fayalite/app

It's bundled in resources since it never really needs to change that much.

to start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests


This all should switch to play but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.

// Data lead tags - data agg classes - data source.