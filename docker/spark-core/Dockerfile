FROM    fayalite:base


RUN mkdir -p /root/spark-provision; \
 mkdir -p /root/spark  mkdir -p /root/persistent-hdfs
WORKDIR /root/spark-provision

RUN wget http://s3.amazonaws.com/spark-related-packages/hadoop-1.0.4.tar.gz
RUN wget http://s3.amazonaws.com/spark-related-packages/spark-1.1.0-bin-hadoop1.tgz

#Spark
RUN tar xvzf spark-*.tgz > /tmp/spark-ec2_spark.log; \
mv spark-1.1.0-bin-hadoop1/* /root/spark;

#Hadoop

RUN tar xvzf hadoop-1.0.4.tar.gz > /tmp/spark-ec2_hadoop.log ; \
mkdir -p /root/ephemeral-hdfs ; \
mv hadoop-1.0.4/* /root/ephemeral-hdfs/ ; \
sed -i 's/-jvm server/-server/g' /root/ephemeral-hdfs/bin/hadoop;

RUN mkdir -p /mnt/ephemeral-hdfs/dfs/name
RUN sed -i 's/PermitEmptyPasswords no/PermitEmptyPasswords yes/g' /etc/ssh/sshd_config

RUN mkdir /root/.ssh/


RUN pip install pexpect
RUN wget http://dl.bintray.com/sbt/debian/sbt-0.13.5.deb; dpkg -i sbt-0.13.5.deb ; \
apt-get install -y libjansi-java ; \
wget http://www.scala-lang.org/files/archive/scala-2.10.3.deb; dpkg -i scala-2.10.3.deb;


RUN ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa ; \
cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys ; \
chmod 640 /root/.ssh/authorized_keys ; \
chmod 700 /root/.ssh/;

#Breaks caching???

RUN sed -i 's/Port 22/Port 22221/g' /etc/ssh/sshd_config


ADD hadoop-native.zip /root/hadoop-native.zip

RUN apt-get install unzip; unzip /root/hadoop-native.zip -d /root; \
cp -r /root/hadoop-native/* /root/ephemeral-hdfs/lib/native/


RUN export NAMENODE_DIR="/mnt/ephemeral-hdfs/dfs/name" ; \
export JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64" ; \
/root/ephemeral-hdfs/bin/hadoop namenode -format

ADD extra_environment /etc/extra_environment

RUN cat /etc/extra_environment >> /root/.bashrc

RUN echo 'JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/' > /etc/environment

ADD init.sh /root/init.sh
RUN chmod +x /root/init.sh


RUN echo 'source /root/init.sh; source /root/.bashrc' >> /etc/init.d/rc.local

RUN ls; \
mkdir -p /mnt/ephemeral-hdfs/logs ; \
mkdir -p /mnt/hadoop-logs ; \
mkdir -p /root/mapreduce

ADD mapreduce/conf /root/mapreduce/conf
ADD ephemeral-hdfs/conf /root/ephemeral-hdfs/conf
ADD sparkconf /root/spark/conf

RUN mkdir -p /mnt/spark
RUN mkdir -p /root/spark-ec2 ; \
echo '127.0.0.1' > /root/spark-ec2/cluster-url

WORKDIR /root/

EXPOSE 8080 4040 22 80 8081 10999 22221 9000