I'm testing a bunch of dynamic scala.js stuff right now. Highly unstable.
I put app (static) and app-dynamic in separate folders,
not multi-project until I can fix scala.js.rx dependency issues

#Run

to compile static-js page

cd fayalite/app

sbt ~fastOptJS

to compile dynamic-js page

cd fayalite/app-dynamic

sbt ~fastOptJS

to start websocket server
Run org.fayalite.ui.WSServer to handle websocket management / serving page

to copy over dynamic resources to make them accessible
(easy to fix this later to be over WS)

Use fswatch to check for changes
fswatch app-dynamic/target/ ./update-dynamic.sh

Or manually copy it
cp ./app-dynamic/target/scala-2.10/*.js* ./app/target/scala-2.10/

Open fayalite/app/index-fastopt.html in browser

Run TestRemoteEval to send dynamic js to get evaluated by client.

#Test

to start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests
Run org.fayalite.ui.ParseServer to handle state management / synchronization
^ Parse server not really implemented. SparkSupervisor is more finished.

All of these should be restartable independently of one another ideally, we'll see if thats
true in testing


#NOTES:
put js in jar for use with served html instead of debug
copy compiled js to resources. Should be served via SimpleServer / route

This all should switch to play but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.


// Data lead tags - data agg classes - data source.