FROM    ubuntu

#Standard
RUN apt-get update; ls;
RUN apt-get install -y --fix-missing python-software-properties python-dev \
software-properties-common \
vim sudo git gcc g++ build-essential \
postfix wget unzip redis-server nano tmux \
psmisc curl net-tools vim-tiny sudo openssh-server ;

RUN apt-get install -y openjdk-7-jre


RUN apt-get install -y python-setuptools ssh; easy_install pip


RUN mkdir -p /root/spark-provision; mkdir -p /root/spark  mkdir -p /root/persistent-hdfs
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

#ADD secret_info/worker /root/.ssh/id_rsa
ADD secret_keys/worker.pub /root/.ssh/worker.pub
#ADD secret_info/worker.pub /root/.ssh/authorized_keys

#chmod 400 /root/.ssh/worker; \
#


RUN cat /root/.ssh/worker.pub >> /root/.ssh/authorized_keys

ADD extra_environment /etc/extra_environment
#RUN cat /etc/extra_environment >> /etc/environment

RUN cat /etc/extra_environment >> /root/.bashrc

RUN echo 'JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/' > /etc/environment

ADD init.sh /root/init.sh
RUN chmod +x /root/init.sh



ADD wrap_sparkshell.py /root/wrap_sparkshell.py

RUN echo 'source /root/init.sh; source /root/.bashrc' >> /etc/init.d/rc.local


RUN ls; \
mkdir -p /mnt/ephemeral-hdfs/logs ; \
mkdir -p /mnt/hadoop-logs

RUN git clone https://github.com/ooyala/spark-jobserver.git ; \
mkdir -p /root/mapreduce

ADD mapreduce/conf /root/mapreduce/conf
ADD ephemeral-hdfs/conf /root/ephemeral-hdfs/conf
ADD sparkconf /root/spark/conf

RUN mkdir -p /mnt/spark
RUN mkdir -p /root/spark-ec2 ; \
echo '127.0.0.1' > /root/spark-ec2/cluster-url

WORKDIR /root/

#NEED HADOOP USER FOR CDH4 hadoop:x:500:501::/home/hadoop:/bin/bash



EXPOSE 8080 4040 22 80 8081 10999 22221 9000
#docker run -P -i --net="host" -t spark /bin/bash
#boot2docker ssh -L 10999:localhost:10999 -L 22:localhost:22221 -L 8080:localhost:8080 -L 4040:localhost:4040 'cd fayalite/spocker; docker kill $(docker ps -a -q); docker rm $(docker ps -a -q); docker run -P -i --net="host" -t spark /bin/bash'
# docker run --net="host" -p 22:22 10999:10999 -t spark
# docker kill $(docker ps -a -q); docker rm $(docker ps -a -q); docker build -t spark .
# NOTE: Remove all rrds which might be around from an earlier run
#rm -rf /var/lib/ganglia/rrds/*
#rm -rf /mnt/ganglia/rrds/*

# Symlink /var/lib/ganglia/rrds to /mnt/ganglia/rrds
#RUN rmdir /var/lib/ganglia/rrds; \
#ln -s /mnt/ganglia/rrds /var/lib/ganglia/rrds
# Make sure rrd storage directory has right permissions
#mkdir -p /mnt/ganglia/rrds
#chown -R nobody:nobody /mnt/ganglia/rrds

# Install ganglia
# TODO: Remove this once the AMI has ganglia by default

#RUN apt-get install ganglia ganglia-web ganglia-gmond ganglia-gmetad
#echo "export HDFS_URL=$HDFS_URL" >> ~/.bash_profile
#$EPHEMERAL_HDFS/sbin/start-dfs.sh
#$EPHEMERAL_HDFS/bin/start-dfs.sh
#C:\\Users\\ryle\\Downloads\\pscp.exe -r -agent root@ec2-50-17-120-247.compute-1.amazonaws.com:/root/hadoop-native .

#SWAP!
#  dd if=/dev/zero of=/mnt/swap bs=1M count=$SWAP_MB
#   mkswap /mnt/swap
#   swapon /mnt/swap
#   echo "Added $SWAP_M MBB swap file /mnt/swap"