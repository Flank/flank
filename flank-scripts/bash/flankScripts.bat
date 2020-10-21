SET DIR=%~dp0
SET scriptsJar=%DIR%\flankScripts.jar

if not exist "%scriptsJar%" {
    %DIR%\buildFlankScripts.bat
}

java -jar "%scriptsJar%" %*
