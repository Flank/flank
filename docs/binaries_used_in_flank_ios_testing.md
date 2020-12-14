# Binaries used in Flank's iOS testing

## Location

Binaries are placed in [Flank binaries repository](https://github.com/Flank/binaries)

## Usage

The binaries are downloaded at runtime when they needed for Linux and Windows from [Flank binaries repository](https://github.com/Flank/binaries).
They are unpacked to `<user directory>/.flank`.
If they already exist on this path, they are not downloaded again.

## Updating

In order to update binaries just follow below steps:
1. checkout binaries [repository](https://github.com/Flank/binaries)
1. update them using:
   - `updateBinariesWithFlankBash` will update binaries for Linux and Windows using `flank-scripts`
   - `update.sh` (old method). It will update binaries for Linux OS
1. commit and push files (create PR with changes)
1. once they will be on master branch. CI job will update artifacts with proper files based on OS
