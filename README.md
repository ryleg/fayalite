```
cat << EOF >> ~/.bash_profile
export FAY=~/Documents/repo/fayalite
alias app="cd $FAY/app-dynamic; sbt ~fastOptJS"
alias ws="cd $FAY; sbt run org.fayalite.MainServer"
EOF
```

MainServer will run org.fayalite.ui.ParseServer to handle state 
management / synchronization and org.fayalite.ui.ws.Server 
to handle websocket management / serving page ( in theory )
In practice right now Workbench (Haoyi Li) serves page.

Copy app-dynamic/index-fastopt.html to path below. Idk why workbench requires
this path, but it won't show up if it's not there.

http://localhost:12345/target/scala-2.11/classes/index-fastopt.html

To start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests
Not fully hooked up to UI yet.

This all should switch to play (maybe?) but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.