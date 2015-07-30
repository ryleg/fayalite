
#Status
@EXPERIMENTAL - unfinished, partially documented. Primary tests /
demonstrations include deployment of a multi-ClassLoader Spark cluster
implemented by modifying all serializers to accept a threadLocalProperty
defining which ClassLoader to use (see http://github.com/ryleg/spark-dynamic 
for the fork of Spark that allows for this
Eventually this will be implemented for all Spark versions using 
macros/reflection to inject Executor.scala changes into the 
cluster on a live JVM). An incremental step would be package overrides in this
repo re-implementing the Spark Executor / SparkContext / JVM Deployment 
with modified serializers, but that may as well be a fork of Spark due to 
versioning / MergeStrategy / packaging issues.

Additional tests include ActorSystem wrappers to make connecting to a remote
Spark ActorSystem easy (Spark does not package the standard Akka
lib but rather a modified version of it, to which your code must conform
to play on the same system,) a websocket / REST server using Spray for
communicating to clients (and replacing the inability of the current Jupyter
ipython/scala setup/API to handle multiple users editing a single kernel in 
real-time). OAuth tests are completed as preliminary for multi-user organizations to 
manage a cluster. A trivial SparkREPLManager is demonstrated for example
on how to manage multiple REPL's within the same JVM, and multiple conflicting
ClassLoader tests are finished (to be documented more.)

Trivial tests / examples for the UI are partially complete in app-dynamic. 
app-template is intended for a hacked pseudo-implementation of a Scala.js REPL.
Jupyter ipython's DOM-based code mirror was taken as inspiration, and for
performance / simplicity Canvas was chosen (for later integrations with
data visualizations in a consistent pattern to match a REPL integrated with
a DAG viewer for Spark pipelines. Some image conversion utilities are also
tested for handling handoff between Canvas image data types and java Image types
for passing binary image data through websocket and back (compression 
unimplemented currently.) Live code injection into a Javascript
client engine tested (inspired by Haoyi Li's workbench) from remote ActorSystem



#Run

Run install.sh which will grab a dynamic version of Spark
with multi-user/multi-classloader cluster patches from 
https://s3-us-west-1.amazonaws.com/fayalite/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar
and put it in your /lib folder.

Add aliases as below to run components separately.

```
export FAY=~/Documents/repo/fayalite
alias app="cd $FAY/app-dynamic; sbt ~fastOptJS"
alias ws="cd $FAY; sbt run org.fayalite.MainServer"
```

Or use ./run.sh (aliases are better so you can see split outputs)

Open in browser:

http://localhost:8080

MainServer will run org.fayalite.ui.ParseServer to handle state 
management / synchronization and org.fayalite.ui.ws.Server 
to handle websocket management / serving page
 
To start additional servers to test other components.
Run org.fayalite.repl.SparkSupervisor to handle driver / repl requests
Not fully hooked up to UI yet.

If you want to test Spark components that rely upon spark-dynamic, make an
assembly binary of spark-dynamic with your changes and copy it to lib/


#Motivation

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

What is lacking is proper notebook execution stream management and 
easy orchestration of displaying concurrently modifiable cells in a format
with configurable compactness (DAG zooming).

As I write a method, a new case class is needed, I should be able to open
a second stream, different in scope than the originating stream, and redirect
client text input into that second stream/scope such that I can instantaneously 
declare the case class as required by my first stream's declaration. 
Intellij aims to do this to some degree, but is vastly restrained by it's linear
file reading (2D box, one document, nothing like cells arranged in a graph),
and inability to reproduce non-linear relationships of many
related code blocks in UI. These streams could be understood as mixing 
in notebook cells with scopes, imports, and DSL mods to make refactoring
easier / editing complex class hierarchies by careful selection and layout
of required sections.

The context switching between these,
including scope referencing (imports, members ..,) should be automatic. 
Bundles of streams could be considered to be 'common scopes' and used for development
of 5-10 or more classes concurrently in ui-parallel by orchestrating their
development through the use of a DAG/REPL

Primary intended use case of this project is for symbolic math
diagrams of data pipeline operations for large scale real time 
machine learning debugging and development. It's use as a conventional
REPL is not primarily intended, but due to the lack of existing alternatives
is encouraged.

Some of these README commentaries will eventually be pulled into
the git wiki for simplification, for now the project is experimental enough
that README revisions should be sufficiently adequate.


#Notes

It is intended to update spark-dynamic to Spark 1.4.0 as soon as possible, 
but the current blocking development factor is the standardization of the UI

Intended integration with ibm spark-kernel comms api eventually.

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