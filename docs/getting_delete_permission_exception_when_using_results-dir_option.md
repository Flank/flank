# Getting delete permission exception when using results-dir option [#790](https://github.com/Flank/flank/issues/790)

##### Changelog
|Date|Who?|Action|
|---|---|---|
|7th July 2020|[pawelpasterz](https://github.com/pawelpasterz)|created|
|   |   |   |

### Description
> I am trying enable code-coverage and seeing this error `java.lang.RuntimeException: com.google.cloud.storage.StorageException: firebase-test-lab@sofi-test.iam.gserviceaccount.com` does not have storage.objects.delete access to cloud-test-sofi-test/coverage_ec/app-debug-androidTest.apk. Does the service account need higher permissions? wondering why delete is needed though (edited)
I also tried with orchestrator, that didn't change anything.

User's yml file
```
gcloud:
   results-bucket: cloud-test-sofi-test
   record-video: true
   app: ../app/build/outputs/apk/debug/app-debug.apk
   test: ../app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
   use-orchestrator: false
   environment-variables:
    endpoint: https://testendponit.com/
   performance-metrics: false
   num-flaky-test-attempts: 1
   device:
   - model: Nexus5X
     version: 26
     locale: en
     orientation: portrait
flank:
   local-result-dir: ../app/build/test-results/ui-tests/
```

> Stacktrace:
```
RunTests
   Uploading app-debug-androidTest.apk .  Uploading app-debug.apk .
 java.lang.RuntimeException: com.google.cloud.storage.StorageException: firebase-test-lab@sofi-test.iam.gserviceaccount.com does not have storage.objects.delete access to cloud-test-sofi-test/coverage_ec/app-debug-androidTest.apk.
 	at ftl.gc.GcStorage.upload(GcStorage.kt:144)
 	at ftl.gc.GcStorage.upload(GcStorage.kt:50)
 	at ftl.run.AndroidTestRunner$resolveApks$2$invokeSuspend$$inlined$forEach$lambda$2.invokeSuspend(AndroidTestRunner.kt:77)
 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
 	at kotlinx.coroutines.DispatchedTask.run(Dispatched.kt:241)
 	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:594)
 	at kotlinx.coroutines.scheduling.CoroutineScheduler.access$runSafely(CoroutineScheduler.kt:60)
 	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:740)
 	Suppressed: java.lang.RuntimeException: com.google.cloud.storage.StorageException: firebase-test-lab@sofi-test.iam.gserviceaccount.com does not have storage.objects.delete access to cloud-test-sofi-test/coverage_ec/app-debug.apk.
 		at ftl.gc.GcStorage.upload(GcStorage.kt:144)
 		at ftl.gc.GcStorage.upload(GcStorage.kt:50)
 		at ftl.run.AndroidTestRunner$resolveApks$2$invokeSuspend$$inlined$forEach$lambda$1.invokeSuspend(AndroidTestRunner.kt:76)
 		... 5 more
 	Caused by: com.google.cloud.storage.StorageException: firebase-test-lab@sofi-test.iam.gserviceaccount.com does not have storage.objects.delete access to cloud-test-sofi-test/coverage_ec/app-debug.apk.
```

### Steps to reproduce
Unfortunately I was unable to reproduce this error (state for 7th July 2020).
If any new information will be available -- this paragraph should be updated.

What was done:
1. new FTL project with admin account + one service account (default editor permissions)
    1. two flank's run with the same apks and `result-dir` (for both admin and service accounts)
    2. first flank run as admin account and second as service account
2. new FTL project with two service accounts (default editor permissions) -- let's call them A & B
    1. first flank run as A and second as B
    2. repeat above but with reversed order
    3. all above with the same apks and `results-dir`

### Comments and thoughts
1. According to google docs `--results-dir` it is recommended use unique value for each run. [Google Docs](https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--results-dir)
2. `Caution: if specified, this argument must be unique for each test matrix you create, otherwise results from multiple test matrices will be overwritten or intermingled.` -- that indicates there is deletion action performed when file with the same name is being uploaded. Therefor `storage.object.delete` is required to do so.
3. [Google Cloud IAM Permissions](https://cloud.google.com/storage/docs/access-control/iam-permissions): `Note: In order to overwrite existing objects, both storage.objects.create and storage.objects.delete permissions are required.` -- as confirmation of above
4. Service account with `Editor` role does not have `storage.object.delete` [Permissions](https://user-images.githubusercontent.com/32893017/86429152-f5c83b00-bcee-11ea-958b-020d8bf3fcee.png)
5. Flank uploads files to storage with the Google client and its logic is rather simple (no additional logic with creating/changing/modifying roles and permissions):

        val fileBlob = BlobInfo.newBuilder(rootGcsBucket, validGcsPath).build()
        storage.create(fileBlob, fileBytes)

6. There might have been some changes on the Google Cloud Storage side with permissions (guessing)
7. There were lots of changes since flank `8.1.0`. We know `8.1.0` version had some issues with upload,caching and overlapping results.

### Conclusion
Some time was spent on this issue but no results, with given input and info, were achieved. Team should track any future issues similar to this one and update doc if any new input will be available.
