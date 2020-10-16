SET DIR=%~dp0
SET scriptsJar=%DIR%\flankScripts.jar

java -jar "%scriptsJar%" %*
