boot2docker ssh -L 10999:localhost:10999 -L 22:localhost:22221 -L 8080:localhost:8080 -L 4040:localhost:4040 'cd spark; docker kill $(docker ps -a -q); docker rm $(docker ps -a -q); docker run -P -i --net="host" -t spark /bin/bash -c "/root/init.sh"'