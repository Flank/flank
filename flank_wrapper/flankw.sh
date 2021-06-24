download_flank() {
  echo "Downloading Flank $1..."
  curl -L https://github.com/Flank/flank/releases/download/$1/flank.jar -o $FLANK_FILE --silent
}

get_remote_revision() {
  curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/flank/flank/commits/v21.06.1 -s | grep '"sha":' | head -n 1 | awk '{print $2}' | sed s/\",//g | sed s/\"//g
}

FLANK_FILE=/usr/local/bin/flank.jar
REMOTE_VERSION=$(curl -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/Flank/flank/releases/latest -s | grep "tag_name" | awk '{print $2}' | sed s/\",//g | sed s/\"//g)

if [ ! -f "$FLANK_FILE" ]; then
  download_flank $REMOTE_VERSION
else
  /usr/bin/env java -jar $FLANK_FILE --version > flank-version

  LOCAL_VERSION=$(cat flank-version | head -1 | awk '{print $2}')
  LOCAL_REVISION=$(cat flank-version | head -2 | tail -1 | awk '{print $2}')

  rm flank-version

  if [[ "$LOCAL_VERSION" != "$REMOTE_VERSION" ]] || [[ "$LOCAL_REVISION" != $(get_remote_revision) ]]; then
    echo "New Flank version is available!"
    download_flank $REMOTE_VERSION
  fi
fi

/usr/bin/env java -jar $FLANK_FILE "$@"
