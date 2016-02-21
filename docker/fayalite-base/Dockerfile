FROM ubuntu

#Allows you to ssh into docker shell
RUN sed -i 's/PermitEmptyPasswords no/PermitEmptyPasswords yes/g' /etc/ssh/sshd_config
RUN mkdir /root/.ssh/
RUN ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa ; \
cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys ; \
chmod 640 /root/.ssh/authorized_keys ; \
chmod 700 /root/.ssh/;

#Changes port to a diff one for convenience on net="host" mode
RUN sed -i 's/Port 22/Port 22221/g' /etc/ssh/sshd_config


#SBT
RUN echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

#Force update
RUN apt-get update

#Common junk
RUN apt-get install -y --fix-missing \
python-software-properties python-dev python-setuptools \
git make gcc autoconf automake sbt \
docker docker-engine libssl-dev \
openjdk-7-jdk software-properties-common build-essential \
postfix wget unzip redis-server nano tmux \
psmisc curl net-tools vim-tiny sudo openssh-server vim \
RUN easy_install pip
RUN pip install pexpect

#Recent spark distro
RUN wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
RUN tar -xzvf ./spark*

