cd ..
./gradlew :multi-modules:multiapp:assembleAndroidTest
./gradlew :multi-modules:multiapp:assemble


if [ "$1" != "" ]; then
    path=/$1
fi

mkdir -p ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks$path
find multi-modules -type f -name "*.apk" -exec cp {} ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks$path \;