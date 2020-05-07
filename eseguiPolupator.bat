@echo off
echo #########################
echo #  Launching Populator  #
ehco #########################
echo.
cd target
java -cp Task3-1.0-SNAPSHOT.jar com.lsmsdb.population.Populator
cd ..
echo.
echo.
echo If no exceptions were thrown it means it worked like a charm!
echo.
pause