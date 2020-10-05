# Cucumber support

Firebase test lab and Flank [do not support](https://firebase.google.com/docs/test-lab/troubleshooting?platform=android#support-for-platforms-android) Cucumber. 
However, you could run these tests. 
 - To make them work properly please disable sharding using `.yml` options:
    ```yaml
    flank:
      disable-sharding: true
    ```
    or by using command-line option
    ```shell script
      --disable-sharding
    ```

 - If you would like to use orchestrator please make sure that you are using at least version `1.3.0` of it.
 - [Currently](https://github.com/Flank/flank/issues/1189), Flank will run Cucumber tests only if there are other Instrumented tests to run in your test apk. 
    In other cases Flank will fast fail with `There are no tests to run` message. 
