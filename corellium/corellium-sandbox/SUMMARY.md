# Research summary

## Flank test run with Corellium instances

### 1. Start VPN
Setup described [here](./README.md#vpn)

VPN is necessary only when using `USBFlux` For corellium client actions (like create instance, upload file etc) it is not required.

Currently, that need to be done manually (setup and connecting)

#### :hammer: ACTION NEEDED :hammer:
Investigate how to start VPN programmatically. Which client supports `ovpn` file. If it should be flank's responsibility to start VPN.

### 2. Start USBFlux
Setup described [here](./README.md#usbfluxd-setup)

For macOS `USBFlux` can be started and killed with:
```
open -a USBFlux
killall USBFlux
```

When it comes to locally compiled binaries, we can use (assuming binary is in `PATH`)
```
sudo usbfluxd
sudo killall usbfluxd
```

### :hammer: ACTION NEEDED :hammer:
How do we want to provide `USBFlux`. Should it be a user responsibility or flank's. Flank could bring required files on its own and compile if needed, but that would require additional libraries being installed on a machine. Maybe flank should have a bunch of pre-compiled binaries?

### 3. Compile and run script
Build script with `./gradlew shadowJar`

Run `java -jar ./corellium/corellium-sandbox/build/libs/corellium-sandbox-all.jar`

TODO

### 4. 

## Test artifacts

### Compiled binaries

[TestApp](./src/main/resources/TestApp) directory contains already compiled binaries with `udid`

```kotlin
bc8a4aaca37b90e72eb46038d6576200fa214cc2
```

Use below configuration if you want to use compiled binaries

```properties
plist_path=./corellium/corellium-sandbox/src/main/resources/com.apple.UIAutomation.plist
xctestrun_path=./corellium/corellium-sandbox/src/main/resources/TestApp/SampleXCUITests_iphoneos14.4-arm64.xctestrun
udid=bc8a4aaca37b90e72eb46038d6576200fa214cc2
```

### iOS project

[Project](./src/main/resources/Project) directory contains ready to build iOS project. To build go
to [Project](./src/main/resources/Project) and run:

```
xcodebuild build-for-testing \
  -allowProvisioningUpdates \
  -project "Sample iOS.xcodeproj" \
  -scheme SampleXCUITests \
  -sdk iphoneos \
  -derivedDataPath ./Compiled
```

Then use below configuration:

```properties
plist_path=./corellium/corellium-sandbox/src/main/resources/com.apple.UIAutomation.plist
xctestrun_path=./corellium/corellium-sandbox/src/main/resources/CompiledProject/SampleXCUITests_iphoneos14.4-arm64.xctestrun
```
