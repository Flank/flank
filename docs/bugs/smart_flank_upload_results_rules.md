# Smart Flank rules of validation result types

Flank trying to avoid override smart-flank-gcs-path by different JUnit report type. That's means: 

1. If user select in ```smart-flank-gcs-path``` command FulJUnitResult.xml and flag ```--full-junit-result``` is not set Flank fail with the message

    ```txt

    smart-flank-gcs-path is set with FullJUnitReport.xml but in this run --full-junit-result is disabled, please set --full-junit-result flag


    ```

2. If user set in ```smart-flank-gcs-path``` command JUnitResult.xml and flag ```--full-junit-result``` is set Flank fails with message

    ```txt

    smart-flank-gcs-path is set with JUnitReport.xml but in this run --full-junit-result enabled, please turn off --full-junit-result flag


    ```

3. If ```smart-flank-gcs-path``` is set to a different name than ```JUnitReport.xml``` and ```FullJUnitReport.xml``` flank not validating report type

4. Flank not validating report type if flag ```--smart-flank-disable-upload``` set
