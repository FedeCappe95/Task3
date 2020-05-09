@echo off
echo #########################
echo #  Launching Populator  #
echo #########################
echo.
java -cp .\target\Task3-1.0-SNAPSHOT.jar com.lsmsdb.task3.Populator --createdb
echo.
echo.
echo If no exceptions were thrown it means it worked like a charm!
echo.
pause