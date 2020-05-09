#!/bin/sh

function pause(){
   read -p "$*"
}

echo "#########################"
echo "#  Launching Populator  #"
echo "#########################"

chmod +x ./target/Task3-1.0-SNAPSHOT.jar
java -cp ./target/Task3-1.0-SNAPSHOT.jar com.lsmsdb.task3.Populator --createdb

echo "If no exceptions were thrown it means it worked like a charm!"

pause "Press [Enter] key to continue..."
