cd app-dynamic; sbt ~fastOptJS

sbt run

Run org.fayalite.ui.ParseServer to handle state management / synchronization
Run org.fayalite.ui.WSServer to handle websocket management / serving page

Open http://localhost:8080 in browser

to compile static-js page

cd fayalite/app

It's bundled in resources since it never really needs to change that much.

to start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests


This all should switch to play but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.

// Data lead tags - data agg classes - data source.