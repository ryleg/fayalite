FROM ubuntu

#Standard
RUN apt-get update; ls;
RUN apt-get install -y --fix-missing python-software-properties python-dev \
software-properties-common \
vim sudo git gcc g++ build-essential \
postfix wget unzip redis-server nano tmux \
psmisc curl net-tools vim-tiny sudo openssh-server ;

RUN apt-get install -y openjdk-7-jre

RUN apt-get install -y python-setuptools ssh; easy_install pip