SET DIR=%~dp0
SET scriptsJar=%DIR%flankScripts.jar

if not exist %scriptsJar% (
    CALL %DIR%\buildFlankScripts.bat
)

java -jar "%scriptsJar%" %*
