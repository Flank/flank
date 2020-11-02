# Flow

Flow starts by parsing .xctestrun file. 
Search for:  `__xctestrun_metadata__` key.

```xml
<key>__xctestrun_metadata__</key>
	<dict>
		<key>FormatVersion</key>
		<integer>1</integer>
	</dict>
```

- **FormatVersion: `1` -** old version of .xctestrun
- **FormatVersion: `2` -** the newest version with test plans
- If format is different than 1 or 2 throw an error.

---

### FormatVersion: 1

Any other key than metadata should have corresponding **TestTarget** dictionary. In example below `EarlGreyExampleSwiftTests` has a **TestTarget** dictionary.

```xml
<plist version="1.0">
<dict>
	<key>EarlGreyExampleSwiftTests</key>
	<dict>
		<key>BlueprintName</key>
		<string>EarlGreyExampleSwiftTests</string>
		... 
	</dict>
	<key>__xctestrun_metadata__</key>
	<dict>
		<key>FormatVersion</key>
		<integer>1</integer>
	</dict>
</dict>
</plist>
```

### FormatVersion: 2

In this version, XML contains two keys: `TestConfigurations` and `TestPlan` in addition to `__xctestrun_metadata__`. 

`TestPlan` is just a dictionary containing basic informations about current **TestPlan.** We can ignore it. So it's excluded from example xml below.

`TestConfigurations` is an array of different test configurations. Test configuration contains name property and array of TestTargets. 

```xml
<plist version="1.0">
<dict>
	<key>Name</key> 
	<string>pl</string> <!-- Name property -->
	<key>TestTargets</key>
	<array> <!-- TestConfigurations -->
		<dict>
			<key>BlueprintName</key>
			<string>UITests</string>
			<!-- Test target and Test configuration properties go here -->
		</dict>
		<dict>
			<key>BlueprintName</key>
			<string>SecondUITests</string>
			<!-- Test target and Test configuration properties go here -->
		</dict>
	</array>
</dict>
</plist>
```

Each configuration may contain different Environment Variables, languages, regions or any other properties. Those properties are stored under TestTarget. 

Currently **FTL** doesn't support specifying TestConfiguration for test execution.

If there is more than one configuration FTL will probably choose one arbitrarily.

For now Flank will allow specifying which test configuration should run with `only-test-configuration` argument.

---

# Running test plan locally

## Build Xcode project

To build example project run command below.

```bash
xcodebuild build-for-testing \
-allowProvisioningUpdates \
-project "FlankMultiTestTargetsExample.xcodeproj" \
-scheme "AllTests" \ #Scheme should have test plans enabled
-derivedDataPath "build_testplan_device" \
-sdk iphoneos | xcpretty
```

This command will generate directory: **Debug-iphoneos** containing binaries and .xctestrun file for each TestPlan. 

In this example scheme `AllTests` has have only one test plan: **AllTests** with two test configurations: `pl` and `en`. 

**Test Plan** contains two **Test Targets: `UITests` and `SecondUITests`**
Outputted .xctestrun should looks like this:

```xml
<plist version="1.0">
<dict>
	<key>TestConfigurations</key>
	<array>
		<dict>
			<key>Name</key>
			<string>en</string>
			<key>TestTargets</key> <!-- Test Targets for `en` test configuration -->
			<array>
				<dict>
					<key>BlueprintName</key>
					<string>UITests</string>
					<key>TestLanguage</key>
					<string>en</string>
					<key>TestRegion</key>
					<string>GB</string> <!-- Language and region -->
					<!-- ... Other properties -->
				</dict>
				<dict>
					<key>BlueprintName</key>
					<string>SecondUITests</string>
					<key>TestLanguage</key>
					<string>en</string>
					<key>TestRegion</key>
					<string>GB</string> <!-- Language and region -->
					<!-- ... Other properties -->
				</dict>
			</array>
		</dict>
		<dict>
			<key>Name</key>
			<string>pl</string>
			<key>TestTargets</key> <!-- Test Targets for `pl` test configuration -->
			<array>
				<dict>
					<key>BlueprintName</key>
					<string>UITests</string>
					<key>TestLanguage</key>
					<string>pl</string>
					<key>TestRegion</key>
					<string>PL</string> <!-- Language and region -->
					<!-- ... Other properties -->
				</dict>
				<dict>
					<key>BlueprintName</key>
					<string>SecondUITests</string>
					<key>TestLanguage</key>
					<string>pl</string>
					<key>TestRegion</key>
					<string>PL</string> <!-- Language and region -->
					<!-- ... Other properties -->
				</dict>
			</array>
		</dict>
	</array>
	<key>TestPlan</key>
	<dict>
		<key>IsDefault</key>
		<true/>
		<key>Name</key>
		<string>AllTests</string>
	</dict>
	<key>__xctestrun_metadata__</key>
	<dict>
		<key>FormatVersion</key>
		<integer>2</integer>
	</dict>
</dict>
</plist>
```

## Running tests on a local device

After generating binaries and .xctestrun file we can run tests using command.

```bash
xcodebuild test-without-building \
-xctestrun "build_testplan_device/Build/Products/testrun.xctestrun" \
-destination "platform=iOS,id=00008030-000209DC1A50802E" \
-only-test-configuration pl | xcpretty
```

Option: `-only-test-configuration pl` allows to specify which test configuration should Xcode run.
