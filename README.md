The goal of this library is to make large data transforms in Spark quick 
and easy to modify / test and to provide extensions to the REPL that 
make multi-user clusters better. 

The eventual long-term goal is collaborative real-time
development and productivity management software for large groups
of programmers working on large-scale code-bases making use of 
per-transform dependency management with more sophisticated classloader
implementations exploiting the override findClass method on the Java
ClassLoader in order to allow CL copying / mixing / static object member
recovery.

Independent user ClassLoaders can be merged together when there are 
no obvious conflicts and conflicts should ideally be resolved using RDD
operation isolation in conjunction with SBT-as-an-api-per-transform philosophy.

Another natural use case would be to make transitions between successive
 code versions allow in-JVM RDD translations across conflicting ClassLoaders.
Additionally, these RDDs should be able to be shared instantaneously 
irrespective of library version across large user bases for rapid development. 

Additionally, eventually notebook cells must be integrated into the DAG 
viewer for fine-grained control over large data pipelines. Ideal MVP
modification of ipython notebooks would be to allow a given class
IO pipe hierarchy DAG to be rendered / zoomable with user input allowing
cells to be expanded for a particular transform or stage, or to inspect
that stage via actions, samples, histograms, etc. in such a way as to allow
visualizations / reports to be generated in-situ on the DAG and preserved 
for future inspection in the same motivation as programming comment 
statements allow for view into code use. Visualizations, samples, and histograms
accompanying RDD transforms are the natural form of commentary
available on a large DAG pipeline.

Final goal is to format and simplify some of the inconveniences of 
REPL coding with macro based DSL mods to language. 
(I.e. macros to autoimport case class members in anonymous 
functions, to set RDD names automatically, to add automatic constructor
methods for turning REPL statements into class hierarchies. 

Some of these README commentaries will eventually be pulled into
the git wiki for simplification, for now the project is experimental enough
that README revisions should be sufficiently adequate.

#Run

Run install.sh which will grab a dynamic version of Spark
with multi-user/multi-classloader cluster patches from 
https://s3-us-west-1.amazonaws.com/fayalite/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar
and put it in your /lib folder. It will also copy app-dynamic/index-fastopt.html
to /target/scala-2.11/classes/index-fastopt.html. I don't know why workbench 
requires this path, but it won't load up if it's not there.

Add aliases as below to run components separately.

```
cat << EOF >> ~/.bash_profile
export FAY=~/Documents/repo/fayalite
alias app="cd $FAY/app-dynamic; sbt ~fastOptJS"
alias ws="cd $FAY; sbt run org.fayalite.MainServer"
EOF
```

Or use ./run.sh (aliases are better so you can see split outputs)

Open in browser:

http://127.0.0.1:12345/target/scala-2.11/classes/index-fastopt.html


MainServer will run org.fayalite.ui.ParseServer to handle state 
management / synchronization and org.fayalite.ui.ws.Server 
to handle websocket management / {serving page ( in theory ) ; 
In practice right now Workbench (Haoyi Li) serves page. }

To start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests
Not fully hooked up to UI yet.

If you want to test Spark components that rely upon spark-dynamic, make an
assembly binary of spark-dynamic with your changes and copy it to lib/

It is intended to update spark-dynamic to Spark 1.4.0 as soon as possible, 
but the current blocking development factor is the standardi

#Notes

This all should switch to play (maybe?) but there are dependency conflicts that must be
resolved the way that spark-notebook resolved them. Did not want to modify
build.sbt yet so I'm using spray.

Etymology - Fayalite is a mineral form of iron silicate used in processed form for
high voltage high frequency transformers. The analogy with large-scale data
 transformations should be completely natural. The pipeline / DAG approach
  takes inspiration from https://github.com/ucb-bar/chisel/ and hopes to extend
  it further to make an efficient representation of DAG data transformations
  follow a pattern usually reserved for more rigorous approaches that 
  require a high degree of analysis, as in circuit diagramming software.