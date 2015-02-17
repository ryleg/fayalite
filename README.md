To load webpage:

#to compile js
cd fayalite/app
sbt
fastOptJS
**has to be put in a separate folder, not multi-project until I can fix scala.js.rx dependency issues


#to start server
From Intellij:
Run org.fayalite.repl.SparkSupervisor #to handle driver / repl requests
Run org.fayalite.ui.ParseServer #to handle state management / synchronization
Run org.fayalite.ui.SimpleServer #to handle websocket management / serving page
**All of these should be restartable independently of one another ideally, we'll see if thats
true in testing

#to test
Open fayalite/app/index-fastopt.html in browser


NOTES:
#put js in jar for use with served html instead of debug
copy compiled js to resources. Served via SimpleServer / route

This all should switch to play but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.

