FROM    fayalite:base


#Setup dirs
RUN mkdir -p /root/spark-provision; \
mkdir -p /root/spark ; \
mkdir -p /root/persistent-hdfs ; \
mkdir -p /mnt/ephemeral-hdfs/logs ; \
mkdir -p /mnt/hadoop-logs ; \
mkdir -p /root/mapreduce ; \

WORKDIR /root/spark-provision

#Spark
RUN wget http://s3.amazonaws.com/spark-related-packages/spark-1.1.0-bin-hadoop1.tgz
RUN tar xvzf spark-*.tgz > /tmp/spark-ec2_spark.log; \
mv spark-1.1.0-bin-hadoop1/* /root/spark;

#Hadoop
RUN wget http://s3.amazonaws.com/spark-related-packages/hadoop-1.0.4.tar.gz
RUN tar xvzf hadoop-1.0.4.tar.gz > /tmp/spark-ec2_hadoop.log ; \
mkdir -p /root/ephemeral-hdfs ; \
mv hadoop-1.0.4/* /root/ephemeral-hdfs/ ; \
sed -i 's/-jvm server/-server/g' /root/ephemeral-hdfs/bin/hadoop;
RUN mkdir -p /mnt/ephemeral-hdfs/dfs/name
RUN wget -O /root/hadoop-native.zip https://s3-us-west-1.amazonaws.com/fayalite/hadoop-native.zip
RUN unzip /root/hadoop-native.zip -d /root; \
cp -r /root/hadoop-native/* /root/ephemeral-hdfs/lib/native/

#Format name node
RUN export NAMENODE_DIR="/mnt/ephemeral-hdfs/dfs/name" ; \
export JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64" ; \
/root/ephemeral-hdfs/bin/hadoop namenode -format

#Env stuff
ADD extra_environment /etc/extra_environment
RUN cat /etc/extra_environment >> /root/.bashrc
RUN echo 'JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/' > /etc/environment
ADD init.sh /root/init.sh
RUN chmod +x /root/init.sh
RUN echo 'source /root/init.sh; source /root/.bashrc' >> /etc/init.d/rc.local

RUN ls; \

ADD mapreduce/conf /root/mapreduce/conf
ADD ephemeral-hdfs/conf /root/ephemeral-hdfs/conf
ADD sparkconf /root/spark/conf

RUN mkdir -p /mnt/spark
RUN mkdir -p /root/spark-ec2 ; \
echo '127.0.0.1' > /root/spark-ec2/cluster-url

WORKDIR /root/

EXPOSE 8080 4040 22 80 8081 10999 22221 9000