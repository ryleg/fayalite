DYNAMIC_JAR="https://s3-us-west-1.amazonaws.com/fayalite/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar"
mkdir lib; wget $DYNAMIC_JAR ./lib
cd app-dynamic; cp ./index-fastopt.html ./target/scala-2.11/classes/index-fastopt.html