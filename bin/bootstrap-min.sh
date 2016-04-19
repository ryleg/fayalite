sudo apt-get install -y \
python-software-properties python-dev python-setuptools \
git make gcc autoconf automake sbt \
openjdk-7-jdk software-properties-common build-essential \
unzip

sudo easy_install pip

#Get spark
wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
tar -xzvf ./spark*

git clone https://github.com/ryleg/fayalite
git clone https://github.com/ryleg/spark
