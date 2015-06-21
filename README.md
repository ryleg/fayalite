Run install.sh which will grab a dynamic version of Spark
with multi-user/multi-classloader cluster patches from 
https://s3-us-west-1.amazonaws.com/fayalite/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar
and put it in your /lib folder. It will also copy app-dynamic/index-fastopt.html
to /target/scala-2.11/classes/index-fastopt.html. Idk why workbench requires 
this path, but it won't load up if it's not there.

Use ./run.sh to start or add aliases as below to run components separately.

```
cat << EOF >> ~/.bash_profile
export FAY=~/Documents/repo/fayalite
alias app="cd $FAY/app-dynamic; sbt ~fastOptJS"
alias ws="cd $FAY; sbt run org.fayalite.MainServer"
EOF
```

Open in browser:

http://localhost:12345/target/scala-2.11/classes/index-fastopt.html


MainServer will run org.fayalite.ui.ParseServer to handle state 
management / synchronization and org.fayalite.ui.ws.Server 
to handle websocket management / {serving page ( in theory ) ; 
In practice right now Workbench (Haoyi Li) serves page. }


To start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests
Not fully hooked up to UI yet.

This all should switch to play (maybe?) but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.

If you want to test Spark components that rely upon spark-dynamic, make an
assembly binary of spark-dynamic with your changes and copy it to lib/