# Update Instructions

Example from:
https://github.com/google/EarlGrey/tree/b1ab378a7ab342f95cb28fe1a391a68ce6e9834b/Demo/EarlGreyExample

- Build with `./build_example.sh`
- Download existing release and extract to `archive`
- Unzip `earlgrey_example.zip`
- Delete the old `xctestrun` files and `Debug-iphoneos` folder into the `archive` folder
- Copy the new files from the unzipped `earlgrey_example.zip` into `archive`
- Copy individual Swift test binary from `Debug-iphoneos/EarlGreyExampleSwiftTests.xctest/EarlGreyExampleSwiftTests` to `swift/EarlGreyExampleSwiftTests`
- Copy individual Swift test binary from `Debug-iphoneos/EarlGreyExampleTests.xctest/EarlGreyExampleTests` to `objc/EarlGreyExampleTests`
