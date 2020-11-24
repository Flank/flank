rm -rf build/

echo "Archiving..."
xcodebuild -project "FlankGameLoopExample.xcodeproj" \
-scheme FlankGameLoopExample \
-sdk iphoneos \
-allowProvisioningUpdates \
archive -archivePath "build/gameloop.xcarchive" | xcpretty

echo "Exporting..."
xcodebuild -exportArchive \
-archivePath "build/gameloop.xcarchive" \
-allowProvisioningUpdates \
-exportOptionsPlist "exportOptions.plist" \
-exportPath "build/ipa/"
