
# For testing, takes a while

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-precise main" | sudo tee -a /etc/apt/sources.list.d/docker.list
sudo apt-get update
sudo apt-get install -y \
python-software-properties python-dev python-setuptools \
git make gcc autoconf automake sbt \
docker docker-engine libssl-dev \
openjdk-7-jdk software-properties-common build-essential \
wget unzip redis-server nano tmux \
psmisc curl net-tools vim-tiny sudo openssh-server vim
sudo easy_install pip

#Get spark
wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
tar -xzvf ./spark*

git clone https://github.com/ryleg/fayalite
git clone https://github.com/ryleg/spark
