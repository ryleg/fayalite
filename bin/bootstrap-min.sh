sudo apt-get update
sudo apt-get install -y git openjdk-7-jdk
git clone https://github.com/ryleg/fayalite
git clone https://github.com/ryleg/spark
export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-amd64/"
export MAVEN_OPTS="-Xmx4g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"


