#!/usr/bin/env bash

#For building spark quick

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823


sudo apt-get update
sudo apt-get install -y git openjdk-7-jdk sbt
git clone https://github.com/ryleg/fayalite
export JAVA_HOME="/usr/lib/jvm/java-1.7.0-openjdk-amd64/"

wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.2-bin-hadoop2.6.tgz
tar -xzvf spark*

wget http://apache.cs.uu.nl/hadoop/common/hadoop-2.6.4/hadoop-2.6.4.tar.gz
tar -xzvf hadoop*


export MAVEN_OPTS="-Xmx4g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"


cat <<EOF > ~/spark-1.6.2-bin-hadoop2.6/conf/spark-defaults.conf
spark.master local[*]
spark.scheduler.mode FAIR
spark.rdd.compress true
spark.driver.memory 4g
EOF

sudo mkfs -t ext4 /dev/xvdf
sudo mount /dev/xvdf /mnt
sudo mkdir /mnt/ephemeral-hdfs

cat <<EOF > ~/hadoop-2.6.4/etc/hadoop/core-site.xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
EOF

cat <<EOF > ~/hadoop-2.6.4/etc/hadoop/hdfs-site.xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir	</name>
        <value>/mnt/ephemeral-hdfs</value>
    </property>
</configuration>
EOF

cat <<EOF > ~/spark-1.6.2-bin-hadoop2.6/conf/core-site.xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
EOF

cat <<EOF > /home/ubuntu/spark-1.6.2-bin-hadoop2.6/conf/spark-env.sh
export HADOOP_CONF_DIR=/home/ubuntu/hadoop-2.6.4/etc/hadoop
export HADOOP_HOME=/home/ubuntu/hadoop-2.6.4/
source /home/ubuntu/.bash_profile
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$HADOOP_HOME/share/hadoop/tools/lib/*
EOF

cat <<EOF >> /home/ubuntu/hadoop-2.6.4/etc/hadoop/hadoop-env.sh
export HADOOP_CONF_DIR=/home/ubuntu/hadoop-2.6.4/etc/hadoop
export HADOOP_HOME=/home/ubuntu/hadoop-2.6.4/
source /home/ubuntu/.bash_profile
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$HADOOP_HOME/share/hadoop/tools/lib/*
EOF

sudo su
sed -i 's/PermitEmptyPasswords no/PermitEmptyPasswords yes/g' /etc/ssh/sshd_config
mkdir /root/.ssh/
ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa ; \
cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys ; \
chmod 640 /root/.ssh/authorized_keys ; \
chmod 700 /root/.ssh/;

bin/hdfs namenode -format

bin/spark-shell --jars /home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/aws-java-sdk-1.7.4.jar,/home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/hadoop-aws-2.6.4.jar,/home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/guava-11.0.2.jar
#git clone https://github.com/ryleg/spark
