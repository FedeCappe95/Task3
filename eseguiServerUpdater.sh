#!/bin/sh

function pause(){
   read -p "$*"
}

chmod +x ./target/Task3-1.0-SNAPSHOT.jar
java -cp ./target/Task3-1.0-SNAPSHOT.jar com.lsmsdb.task3.MainServerUpdater

pause "Press [Enter] key to continue..."
