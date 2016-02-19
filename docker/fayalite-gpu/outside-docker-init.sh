#!/bin/bash

#Install docker and build tools
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
sudo sh -c "echo deb https://get.docker.com/ubuntu docker main > /etc/apt/sources.list.d/docker.list"
sudo apt-get update && sudo apt-get install -y lxc-docker
sudo apt-get update && sudo apt-get install -y build-essential

#Get nvidia binaries.
wget http://developer.download.nvidia.com/compute/cuda/6_5/rel/installers/cuda_6.5.14_linux_64.run
chmod +x cuda_6.5.14_linux_64.run
mkdir nvidia_installers
./cuda_6.5.14_linux_64.run -extract=`pwd`/nvidia_installers

#Get nvidia deps
sudo apt-get install -y linux-image-extra-virtual

#Not sure if this is necessary now or just once along with later reboots
reboot

#Either copy/paste the conf from this directory in here
#sudo nano /etc/modprobe.d/blacklist-nouveau.conf
#Or copy it directly
sudo cp ./blacklist-nouveau.conf /etc/modprobe.d/blacklist-nouveau.conf

sudo su # for pipe
echo options nouveau modeset=0 | tee -a /etc/modprobe.d/nouveau-kms.conf

update-initramfs -u

#IDK if this reboot is necessary same as earlier issue.
reboot

sudo apt-get install -y linux-source
#sudo apt-get install -y linux-headers-3.13.0-37-generic # this might have come from install page?
#I don't think I ran ^^^ that command the time it worked
sudo apt-get install linux-headers-generic


#Now to install NVIDIA drivers

#Inspection tool for gfx
sudo apt-get install mesa-utils

glxinfo | grep direct
glxgears

#This doesn't appear to work in Docker on Mac because you can't install nvidia drivers
#See http://stackoverflow.com/questions/2260931/cuda-program-on-vmware


