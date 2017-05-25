# Flank

Flank is a [Firebase Test Lab](https://firebase.google.com/docs/test-lab/?gclid=CjwKEAiA0fnFBRC6g8rgmICvrw0SJADx1_zAFTUPL4ffVSc5srwKT_Up4vJb15Ik4iIxIK4bQ5J-vxoCIS3w_wcB) tool for massively-scaling your automated Android tests. Run large test suites across many devices/versions/configurations at the same time, in parallel. Flank can easily be used in a CI environment where Gradle (or similar) first builds the APK:s and then Flank is used to execute the tests. 

To use Flank, please sign up for Firebase Test Lab and install the Google Cloud SDK. 

### Setup

* Install the [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

* Signup for [Firebase Test Lab](https://firebase.google.com/)

* Install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/)

* Download [Flank](https://bintray.com/flank1/Flank/download_file?file_path=Flank-1.0.6.jar)


To runs tests with Flank you will need the app and test apk's. You also need to specify in which package you would like tests to run. A single class or test can also be executed (package_name.class_name#method_name). Usage:

```
java -jar Flank-1.0.6.jar <app-apk> <test-apk> <package-name>
```

When the executions are completed Flanks will fetch the xml result files and store them in a folder named: ```results```. 

### Configure Flank 

It's possible to configure Flank by including a Java properties file in the root folder: ```config.properties```

Following properties can be configured:

```
deviceIds: The ID:s of the devices to run the tests on
os-version-ids: The API-levels of the devices to run the tests on
orientations: The orientations, portrait, landscape or both
locales: The device locales
shard-timeout: Timeout in minutes for each shard 
shard-duration: Duration in seconds for each shard

numShards: Number of shards
shardIndex: If a specific shard should be executed
debug-prints: If debug prints should be enabled
gcloud-path: The path to the glcoud binary
gsutil-path: The path to the gsutil binary

```

Example of a properties file:

```
deviceIds=Nexus5X,Nexus6P  
os-version-ids=23,24   
orientations=portrait  
locales=en,sv  
shard-timeout=5 
shard-duration=120  

numShards=  
shardIndex= 
debug-prints=false  
gcloud-path=
gsutil-path=

```

### Configurable Shards

Flank supports configurable shard durations. Instead of creating one shard per test case Flank can create shards that contain tests that add up to a given duration. This feature was introduced to make Flank faster while also saving cost (seconds are round up to minutes by Firebase test lab). By default Flank will try to create shards with tests adding up to 2 minutes in execution time. 

First time a new app is tested each test is run in its own shard so that Flank can save the individual execution times. This is also the case for new tests that are introduced. The execution times for tests are saved in a file: ```flank.tests```. This file is used by Flank to create shards and is backed up in your google cloud bucket. Open the file in an editor to edit the execution times. Create an empty ```flank.tests``` in the root Flank folder to start from a clean slate. 

```shard-duration``` can be set in ```flank.properties```. Default shard duration is 120 seconds. To enable one test per shard set ```shard-duration``` to -1 or specify a custom number of shards with ```numShards```. 

### Troubleshooting

Please enable debug mode ```debug-prints:true``` in the properties file if it's not working correctly. 

If Flank hangs and nothing seems to be happening (even when debug mode is enabled) make sure the correct user (registered with Firebase) is logged into Firebase. Please also see [Google Cloud Storage Authentication](https://cloud.google.com/storage/docs/authentication).

If you see `AccessDeniedException: 403` you either need to upgrade your Firebase project to a paid subscription or its due to your Firebase account and project ID not being in sync.

Follow below steps to set it:

```
gcloud auth login

To set gcloud to the correct google account. 

Then please use:

gcloud config set project <idOfYourProject>

```
Make sure that `<idOfYourProject>` belongs to a project that is subscribed to either Flame or Blaze (Paid subscriptions). 

If you are using the terminal in OS X and Flank gets stuck at around 250 shards its because of a setting in OS X. To fix it [please follow these instructions](https://blog.dekstroza.io/ulimit-shenanigans-on-osx-el-capitan/).
