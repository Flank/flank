# Rate limit exceeded [#891](https://github.com/Flank/flank/issues/891)
Reported description:
> After bump from 20.05.2 to 20.06.2 I started to see some issues related to rate limit. Also, there are no significant changes on the shard size or test amount.

> We are running another 7 flank execution in parallel. Total ~6k tests.

## Stack trace
```
java.io.IOException: Request failed
  at ftl.http.ExecuteWithRetryKt$executeWithRetry$$inlined$withRetry$1.invokeSuspend(ExecuteWithRetry.kt:36)
  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
  at kotlinx.coroutines.DispatchedTask.run(Dispatched.kt:241)
  at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:270)
  at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:79)
  at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:54)
  at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
  at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:36)
  at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
  at ftl.http.ExecuteWithRetryKt.executeWithRetry(ExecuteWithRetry.kt:41)
  at ftl.gc.GcToolResults.getStepResult(GcToolResults.kt:98)
  at ftl.json.SavedMatrix.finished(SavedMatrix.kt:90)
  at ftl.json.SavedMatrix.update(SavedMatrix.kt:65)
  at ftl.json.MatrixMapKt.update(MatrixMap.kt:47)
  at ftl.run.NewTestRunKt$newTestRun$2$2.invokeSuspend(NewTestRun.kt:24)
  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
  at kotlinx.coroutines.ResumeModeKt.resumeUninterceptedMode(ResumeMode.kt:45)
  at kotlinx.coroutines.internal.ScopeCoroutine.afterCompletionInternal(Scopes.kt:32)
  at kotlinx.coroutines.JobSupport.completeStateFinalization(JobSupport.kt:310)
  at kotlinx.coroutines.JobSupport.tryFinalizeSimpleState(JobSupport.kt:276)
  at kotlinx.coroutines.JobSupport.tryMakeCompleting(JobSupport.kt:807)
  at kotlinx.coroutines.JobSupport.makeCompletingOnce$kotlinx_coroutines_core(JobSupport.kt:787)
  at kotlinx.coroutines.AbstractCoroutine.resumeWith(AbstractCoroutine.kt:111)
  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:46)
  at kotlinx.coroutines.ResumeModeKt.resumeUninterceptedMode(ResumeMode.kt:45)
  at kotlinx.coroutines.internal.ScopeCoroutine.afterCompletionInternal(Scopes.kt:32)
  at kotlinx.coroutines.JobSupport.completeStateFinalization(JobSupport.kt:310)
  at kotlinx.coroutines.JobSupport.tryFinalizeFinishingState(JobSupport.kt:236)
  at kotlinx.coroutines.JobSupport.tryMakeCompletingSlowPath(JobSupport.kt:849)
  at kotlinx.coroutines.JobSupport.tryMakeCompleting(JobSupport.kt:811)
  at kotlinx.coroutines.JobSupport.makeCompletingOnce$kotlinx_coroutines_core(JobSupport.kt:787)
  at kotlinx.coroutines.AbstractCoroutine.resumeWith(AbstractCoroutine.kt:111)
  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:46)
  at kotlinx.coroutines.DispatchedTask.run(Dispatched.kt:241)
  at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:270)
  at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:79)
  at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:54)
  at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
  at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:36)
  at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
  at ftl.cli.firebase.test.android.AndroidRunCommand.run(AndroidRunCommand.kt:48)
  at picocli.CommandLine.executeUserObject(CommandLine.java:1769)
  at picocli.CommandLine.access$900(CommandLine.java:145)
  at picocli.CommandLine$RunLast.executeUserObjectOfLastSubcommandWithSameParent(CommandLine.java:2150)
  at picocli.CommandLine$RunLast.handle(CommandLine.java:2144)
  at picocli.CommandLine$RunLast.handle(CommandLine.java:2108)
  at picocli.CommandLine$AbstractParseResultHandler.execute(CommandLine.java:1975)
  at picocli.CommandLine.execute(CommandLine.java:1904)
  at ftl.Main$Companion$main$1.invoke(Main.kt:53)
  at ftl.Main$Companion$main$1.invoke(Main.kt:43)
  at ftl.util.Utils.withGlobalExceptionHandling(Utils.kt:130)
  at ftl.Main$Companion.main(Main.kt:49)
  at ftl.Main.main(Main.kt)
C used by: com.google.api.client.googleapis.json.GoogleJsonResponseException: 429 Too Many Requests
{ 
  "code" : 429,
  "errors" : [ {
    "domain" : "global",
    "message" : "Quota exceeded for quota group 'default' and limit 'Queries per user per 100 seconds' of service 'toolresults.googleapis.com' for consumer 'project_number:x'.",
    "reason" : "rateLimitExceeded"
  } ],
  "message" : "Quota exceeded for quota group 'default' and limit 'Queries per user per 100 seconds' of service 'toolresults.googleapis.com' for consumer 'project_number:x'.",
  "status" : "RESOURCE_EXHAUSTED"
} 
  at com.google.api.client.googleapis.json.GoogleJsonResponseException.from(GoogleJsonResponseException.java:150)
  at com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest.newExceptionOnError(AbstractGoogleJsonClientRequest.java:113)
  at com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest.newExceptionOnError(AbstractGoogleJsonClientRequest.java:40)
  at com.google.api.client.googleapis.services.AbstractGoogleClientRequest$1.interceptResponse(AbstractGoogleClientRequest.java:443)
  at com.google.api.client.http.HttpRequest.execute(HttpRequest.java:1092)
  at com.google.api.client.googleapis.services.AbstractGoogleClientRequest.executeUnparsed(AbstractGoogleClientRequest.java:541)
  at com.google.api.client.googleapis.services.AbstractGoogleClientRequest.executeUnparsed(AbstractGoogleClientRequest.java:474)
  at com.google.api.client.googleapis.services.AbstractGoogleClientRequest.execute(AbstractGoogleClientRequest.java:591)
  at ftl.http.ExecuteWithRetryKt$executeWithRetry$$inlined$withRetry$1.invokeSuspend(ExecuteWithRetry.kt:41)
  ... 52 more   
```

## Reported flank config
```yaml
    flank:
      max-test-shards: 15
      shard-time: 160
      num-test-runs: 1
      smart-flank-gcs-path: gs://x/unit-tests.xml
      smart-flank-disable-upload: false
      files-to-download:
      test-targets-always-run:
      disable-sharding: false
      project: x
      local-result-dir: results
      full-junit-result: true
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
      run-timeout: -1
      legacy-junit-result: false
      ignore-failed-tests: false
      output-style: multi

RunTests

 Smart Flank cache hit: 100% (88 / 88)
  Shard times: 93s, 93s

  Uploadingx-app-debug.apk .
  Uploading x-androidTest.apk .
  88 tests / 2 shards
```

## To Reproduce
```shell script
Flank.jar firebase test android run --num-flaky-test-attempts=0 --full-junit-result=true
```

## API usage outline
Real examples simplified to pseudo code that outlines only API call usage 
#### Gcloud
Case for disabled sharding
* [MonitorTestExecutionProgress](https://github.com/Flank/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/matrix_ops.py#L164)
```
while (matrix status is not completed) {
    _client.projects_testMatrices.Get(
        TestingProjectsTestMatricesGetRequest(projectId, testMatrixId)
    )
    wait(6s)
}
```
* [MonitorTestMatrixProgress](https://github.com/Flank/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/matrix_ops.py#L248)
Case for enabled sharding  
```
while (matrix status is not completed) {
    _client.projects_testMatrices.Get(
        TestingProjectsTestMatricesGetRequest(projectId, testMatrixId)
    )
    wait(6s)
}
```

#### Flank v20.06.2
* [pollMatrices](https://github.com/Flank/flank/blob/6ee6939263923953edf67afa7218cf2c496c2ef2/test_runner/src/main/kotlin/ftl/run/common/PollMatrices.kt#L19)
```
forEach(test matrix) {
    async while(matrix status is not completed) {
        GcTestMatrix.refresh(testMatrixId, projectId)
        wait(5s)
    }
}
```
* [SavedMatrix.finished](https://github.com/Flank/flank/blob/c88cb2786de67c0a114fc31a7b25917a035e145b/test_runner/src/main/kotlin/ftl/json/SavedMatrix.kt#L75)
```
forEach(test matrix) {
    sync forEach(matrix test execution) {
        GcToolResults.listTestCases(toolResultsStep)
        GcToolResults.getStepResult(toolResultsStep)
    }
    sync forEach(matrix test execution) {
        GcToolResults.getExecutionResult(testExecution)
        GcToolResults.getStepResult(toolResultsStep)
    }
}
```

#### Flank v20.05.2
* [pollMatrices](https://github.com/Flank/flank/blob/accca3b941874e9556eea6616b34a9f4319c8746/test_runner/src/main/kotlin/ftl/run/common/PollMatrices.kt#L15)
```
// Only the first matrix is getting status updates
// the others are blocked until the first is getting completed.
// As a result, it reduces the amount of requests to 1 per 5 secs.
// That is why this version of the flank is not getting limit exceeded.
forEach(test matrix) {
    sync while(matrix status is not completed) {
        GcTestMatrix.refresh(testMatrixId, projectId)
        wait(5s)
    }
}
```

#### Flank v20.06.2
* [pollMatrices](https://github.com/Flank/flank/blob/6ee6939263923953edf67afa7218cf2c496c2ef2/test_runner/src/main/kotlin/ftl/run/common/PollMatrices.kt#L19)
```
forEach(test matrix) {
    async while(matrix status is not completed) {
        GcTestMatrix.refresh(testMatrixId, projectId)
        wait(5s)
    }
}
```
* [SavedMatrix.finished](https://github.com/Flank/flank/blob/c88cb2786de67c0a114fc31a7b25917a035e145b/test_runner/src/main/kotlin/ftl/json/SavedMatrix.kt#L75)
```
forEach(test matrix) {
    sync forEach(matrix test execution) {
        GcToolResults.getStepResult(toolResultsStep)
    }  
}
```

## API calls usage comparison table
Following table should compare API calls complexity.

|         | execution status updates |
|:-------:|:------------------------:|
| Gcloud  | 1 * r / 6s + 1           |
| 20.05.2 | 1 * r / 5s + (E * r)     |
| 20.06.2 | M * r / 5s + (M * E * 4 * r) |
 
```
r - request
s - second
M - count of matrixes
E - count of test executions in matrix scope
```

## Conclusions
#### Gcloud
Because of single matrix run gives only 1 request per 6 seconds

#### Flank v20.05.2
Gets matrix status updates in synchronize way so only the first matrix is getting status updates,
the others are blocked until the first is getting completed, so the amount of requests is reduced to 1 per 5 secs.
Plus additional number of requests for each execution in one matrix scope.
That is why this version of the flank is not getting a limit exceeded.

#### Flank v20.06.2
Is polling results asynchronously for each matrix. 
At last, it is doing a request for each test execution for each matrix, 
and this is the place where flank is getting a rate limit exceeded.
It is visible on stack trace.
```
  at ftl.gc.GcToolResults.getStepResult(GcToolResults.kt:98)
  at ftl.json.SavedMatrix.finished(SavedMatrix.kt:90)
```
