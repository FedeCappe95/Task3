#!/bin/sh

function pause(){
   read -p "$*"
}

echo "#########################"
echo "#  Launching Populator  #"
echo "#########################"

java -cp ./target/Task3-1.0-SNAPSHOT.jar com.lsmsdb.population.Populator

echo "If no exceptions were thrown it means it worked like a charm!"

pause "Press [Enter] key to continue..."