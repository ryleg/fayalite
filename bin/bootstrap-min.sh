#!/usr/bin/env bash

#For building spark quick

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823


sudo apt-get update
sudo apt-get install -y git openjdk-7-jdk sbt
git clone https://github.com/ryleg/fayalite
export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-amd64/"



export MAVEN_OPTS="-Xmx4g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"




#git clone https://github.com/ryleg/spark
