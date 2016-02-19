FROM ubuntu

RUN echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
RUN echo "deb https://apt.dockerproject.org/repo ubuntu-precise main" | sudo tee -a /etc/apt/sources.list.d/docker.list
RUN apt-get update
RUN apt-get install -y --fix-missing \
python-software-properties python-dev python-setuptools \
git make gcc autoconf automake sbt \
docker docker-engine libssl-dev \
openjdk-7-jdk software-properties-common build-essential \
postfix wget unzip redis-server nano tmux \
psmisc curl net-tools vim-tiny sudo openssh-server vim
RUN easy_install pip
RUN wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
RUN tar -xzvf ./spark*

