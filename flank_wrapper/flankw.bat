ECHO off

SET FLANK_FILE=%APPDATA%\.flank\flank.jar

curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/Flank/flank/releases/latest -s > flank_tmp

FOR /f "delims=" %%i IN ('findstr /R tag_name flank_tmp') DO SET REMOTE_VERSION=%%i

SET REMOTE_VERSION=%REMOTE_VERSION:*": =%
SET REMOTE_VERSION=%REMOTE_VERSION:"=%
SET REMOTE_VERSION=%REMOTE_VERSION:,=%

curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/flank/flank/commits/%REMOTE_VERSION% -s > flank_tmp

findstr /R sha flank_tmp > flank_rev

SET /P REMOTE_REVISION=<flank_rev
DEL flank_rev

SET REMOTE_REVISION=%REMOTE_REVISION:*": =%
SET REMOTE_REVISION=%REMOTE_REVISION:"=%
SET REMOTE_REVISION=%REMOTE_REVISION:,=%


ECHO Remote revision: %REMOTE_REVISION%
ECHO Remote version: %REMOTE_VERSION%

IF NOT EXIST %FLANK_FILE% (
	GOTO :downloadFlank
) ELSE (
	ECHO Flank found
)


java -jar %FLANK_FILE% --version > flank_tmp

FOR /f "delims=" %%i IN ('findstr revision flank_tmp') DO SET LOCAL_REVISION=%%i
SET LOCAL_REVISION=%LOCAL_REVISION:*: =%
ECHO Local revision: %LOCAL_REVISION%

FOR /f "delims=" %%i IN ('findstr /R version flank_tmp') DO SET LOCAL_VERSION=%%i
SET LOCAL_VERSION=%LOCAL_VERSION:*: =%
ECHO Local version: %LOCAL_VERSION%

IF NOT "%LOCAL_VERSION%" == "%REMOTE_VERSION%" (
	GOTO :downloadFlank
)
IF NOT "%LOCAL_REVISION%" == "%REMOTE_REVISION%" (
	GOTO :downloadFlank
)

ECHO Flank is up to date

:EXIT
DEL flank_tmp
EXIT

:downloadFLank
ECHO Downloading Flank %REMOTE_VERSION%
curl -L https://github.com/Flank/flank/releases/download/%REMOTE_VERSION%/flank.jar -o %FLANK_FILE%
GOTO :EXIT

