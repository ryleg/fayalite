
# Look up campagnol vpn. It's useful for creating vpns everywhere.
# Still haven't automated deployment but it makes Spark clusters easy
# to setup p2p

wget http://downloads.sourceforge.net/project/campagnol/campagnol/0.3.5/campagnol-0.3.5.tar.bz2?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fcampagnol%2Ffiles%2F&ts=1454107875&use_mirror=vorboss
mv camp* camp
cd camp*
./configure
make
sudo make install
