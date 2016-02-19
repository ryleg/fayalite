wget http://developer.download.nvidia.com/compute/cuda/6_5/rel/installers/cuda_6.5.14_linux_64.run
chmod +x cuda_6.5.14_linux_64.run
mkdir nvidia_installers
./cuda_6.5.14_linux_64.run -extract=`pwd`/nvidia_installers
cd nvidia_installers

#THIS WONT WORK FIRST TIME
sudo ./NVIDIA-Linux-x86_64-340.29.run


sudo apt-get install -y linux-image-extra-virtual
reboot
sudo nano /etc/modprobe.d/blacklist-nouveau.conf

blacklist nouveau
blacklist lbm-nouveau
options nouveau modeset=0
alias nouveau off
alias lbm-nouveau off

sudo su # for pipe
echo options nouveau modeset=0 | tee -a /etc/modprobe.d/nouveau-kms.conf

update-initramfs -u
reboot

sudo apt-get install -y linux-source
#sudo apt-get install -y linux-headers-3.13.0-37-generic
sudo apt-get install -y linux-headers-generic

cd nvidia_installers
sudo ./NVIDIA-Linux-x86_64-340.29.run


sudo modprobe nvidia

./cuda-linux64-rel-6.5.14-18749181.run
sudo ./cuda-samples-linux-6.5.14-18745345.run


cd /usr/local/cuda/samples/1_Utilities/deviceQuery
make
./deviceQuery


ldconfig /usr/local/cuda /usr/local/cuda/lib /usr/local/cuda/lib64 /home/ubuntu/jclnx/JCuda-All-0.6.5
export LD_LIBRARY_PATH="/usr/local/cuda:/usr/local/cuda/lib:/usr/local/cuda/lib64:/home/ubuntu/jclnx/JCuda-All-0.6.5-bin-linux-x86_64"

export LD_LIBRARY_PATH="/usr/local/cuda/lib:/usr/local/cuda/lib64:/home/ubuntu/jclnx/JCuda-All-0.6.5-bin-linux-x86_64"

export LD_LIBRARY_PATH="/home/ubuntu/jclnx/JCuda-All-0.6.5-bin-linux-x86_64"


scp -i -r
--exclude 'target/streams'
rsync -vzr -e "ssh -i $HOME/.ssh/spark.pem -o StrictHostKeyChecking=no" scripts ubuntu@$GPU:~/

sudo docker run -ti --device /dev/nvidia0:/dev/nvidia0 --device /dev/nvidiactl:/dev/nvidiactl --device /dev/nvidia-uvm:/dev/nvidia-uvm -i --net="host" -P -t spark:core /bin/bash

 sudo docker run -ti --device /dev/nvidia0:/dev/nvidia0 \
 --device /dev/nvidiactl:/dev/nvidiactl --device /dev/nvidia-uvm:/dev/nvidia-uvm \
 -i --net="host" -P -v ~/:/home/ubuntu \
 -v /usr/local/cuda:/usr/local/cuda \
 -v /usr/lib32:/hostlib32 \
 -v /usr/lib:/hostlib \
 -t spark:core /bin/bash

 export LD_LIBRARY_PATH="/hostlib32:/hostlib:/usr/local/cuda/lib:/usr/local/cuda/lib64:/home/ubuntu/jclnx/JCuda-All-0.6.5-bin-linux-x86_64"

java -cp /home/ubuntu/torch.jar JCublasSample