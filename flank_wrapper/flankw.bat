echo off

SET FLANK_FILE=%APPDATA%\.flank\flank.jar

curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/Flank/flank/releases/latest -s > flank_tmp

for /f "delims=" %%i in ('findstr /R tag_name flank_tmp') do set REMOTE_VERSION=%%i

SET REMOTE_VERSION=%REMOTE_VERSION:*": =%
SET REMOTE_VERSION=%REMOTE_VERSION:"=%
SET REMOTE_VERSION=%REMOTE_VERSION:,=%

echo Remote version: %REMOTE_VERSION%

if not exist %FLANK_FILE% (
	goto :downloadFlank
) else (
	echo Flank found
)


java -jar %FLANK_FILE% --version > tmp.txt
for /f "delims=" %%i in ('findstr /R version flank_tmp') do set LOCAL_VERSION=%%i
SET LOCAL_VERSION=%LOCAL_VERSION:*: =%
echo %LOCAL_VERSION%

for /f "delims=" %%i in ('findstr /R revision flank_tmp') do set LOCAL_REVISION=%%i
SET LOCAL_REVISION=%LOCAL_REVISION:*: =%
echo %LOCAL_REVISION%

if %LOCAL_VERSION% != %REMOTE_VERSION% (
	goto :downloadFlank
)

exit

:downloadFLank
echo Downloading Flank %REMOTE_VERSION%
curl -L https://github.com/Flank/flank/releases/download/%REMOTE_VERSION%/flank.jar -o %FLANK_FILE%

:remoteRevision
curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/flank/flank/commits/%REMOTE_VERSION%/ -s
