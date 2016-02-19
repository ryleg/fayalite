#!/bin/bash

#Install docker and build tools
#Get nvidia binaries.
wget http://developer.download.nvidia.com/compute/cuda/6_5/rel/installers/cuda_6.5.14_linux_64.run
chmod +x cuda_6.5.14_linux_64.run
mkdir nvidia_installers
./cuda_6.5.14_linux_64.run -extract=`pwd`/nvidia_installers

#Get nvidia deps
sudo apt-get install -y linux-image-extra-virtual

reboot

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
sudo apt-get install mesa-utils

glxinfo | grep direct
glxgears

#See http://stackoverflow.com/questions/2260931/cuda-program-on-vmware


