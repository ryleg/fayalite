
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