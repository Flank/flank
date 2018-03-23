## Release process

- Update `release_notes.md` with changes for Flank Java.
- Bump the version number in `Flank/build.gradle` (example: `version '2.0.1'`)
- Search and replace old version in `README.md` (example: `Flank-2.0.1.jar`)
- Publish new release to bintray
- Create GitHub tag and publish the release with release notes
