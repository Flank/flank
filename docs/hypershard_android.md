# Hypershard Android

This is an introduction and breakdown of the steps required to make use of [Hypershard-android](https://github.com/dropbox/hypershard-android).

It should be used in cases where an application has a significant number of UI tests that need to be sharded correctly to run efficiently.
On Android UI tests sharding require the entire application to be built first. The hypershard library removes the need for building and outputs a list of all available tests.

More information on this can be found at the official library: [Hypershard-android](https://github.com/dropbox/hypershard-android)


### Installation

1. Releases can be found downloaded from [Maven Central](https://search.maven.org/search?q=g:com.dropbox.mobile.hypershard)
    1. Ensure that you download the correct suffixed as follows `*-all.jar` library
2. Alternatively cloning the repository [github.com/dropbox/hypershard-android](https://github.com/dropbox/hypershard-android) 
    1. Then running `./gradlew install` will produce a valid jarfile in the build directory.

### Running

1. Open Terminal/cmd/bash

2. Make sure that the hypershard jar file is either copied to the correct location and/or available on the path. 

3. Hypershard options are as follows:
    ```bash
   >java -jar hypershard-1.1.2-all.jar --help
   Usage: hypershardcommand [OPTIONS] [dirs]...
   
     Hypershard is a fast and simple test collector that uses the Kotlin and Java
     ASTs. Hypershard CLI will print full qualified test names found in dir(s).
   
   Options:
     --annotation-name TEXT      Class annotation name to process. For example,
                                 if this was set to 'UiTest', then Hypershard
                                 will only process classes annotated with
                                 @UiTest.
     --not-annotation-name TEXT  Class annotation name *not* to process. For
                                 example, if this was set to 'UiTest', then
                                 Hypershard will *not* process classes annotated
                                 with @UiTest.
     -h, --help                  Show this message and exit
   
   Arguments:
     dirs  Dir(s) to process. The location of the test classes to parse
   ```

3. An example run of hypershard for flank where the current directory is flank root:

   ```bash
   >java -jar hypershard-1.1.2-all.jar ./test_runner/src/test/kotlin/ftl/reports/util/
   ```

   Results in the following output
   ```bash
   ftl.reports.util.EndsWithTextWithOptionalSlashAtTheEndTest.should properly found end suffix matching text
   ```
4. Which can be saved by simple bash commands such as the > character for example:
    ```bash
       >java -jar hypershard-1.1.2-all.jar ./test_runner/src/test/kotlin/ftl/reports/util/ > hypershardtests
    ```
   
### More examples

A python usage example can be found: https://github.com/dropbox/hypershard-android/tree/master/example

### Resources

https://github.com/dropbox/hypershard-android