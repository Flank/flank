package flank.corellium.cli.run.test.android

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.instrument.log.Instrument
import flank.log.Event
import flank.log.event
import flank.log.invoke
import flank.log.output
import org.junit.Assert
import org.junit.Test
import java.io.File

class FormatKtTest {

    /**
     * Test is checking if all specified events have registered dedicated formatters.
     * Also, the specified events should be formatted and printed to console output.
     */
    @Test
    fun test() {
        // ======================== GIVEN ========================

        val events = listOf(
            RunTestCorelliumAndroid.Authorize event Event.Start,
            RunTestCorelliumAndroid.CleanUp event Event.Start,
            RunTestCorelliumAndroid.OutputDir event Event.Start,
            RunTestCorelliumAndroid.DumpShards event Event.Start,
            RunTestCorelliumAndroid.ExecuteTests event Event.Start,
            RunTestCorelliumAndroid.CompleteTests event Event.Start,
            RunTestCorelliumAndroid.GenerateReport event Event.Start,
            RunTestCorelliumAndroid.InstallApks event Event.Start,
            RunTestCorelliumAndroid.InvokeDevices event Event.Start,
            RunTestCorelliumAndroid.LoadPreviousDurations event Event.Start,
            RunTestCorelliumAndroid.ParseApkInfo event Event.Start,
            RunTestCorelliumAndroid.ParseTestCases event Event.Start,
            RunTestCorelliumAndroid.PrepareShards event Event.Start,
            Unit event RunTestCorelliumAndroid.LoadPreviousDurations.Searching(5),
            Unit event RunTestCorelliumAndroid.LoadPreviousDurations.Summary(1, 2, 3),
            Unit event RunTestCorelliumAndroid.InstallApks.Status(AndroidApps.Event.Connecting.Agent("123456")),
            Unit event RunTestCorelliumAndroid.InstallApks.Status(AndroidApps.Event.Connecting.Console("123456")),
            Unit event RunTestCorelliumAndroid.InstallApks.Status(AndroidApps.Event.Apk.Uploading("123456", "path/to/apk.apk")),
            Unit event RunTestCorelliumAndroid.InstallApks.Status(AndroidApps.Event.Apk.Installing("123456", "path/to/apk.apk")),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.GettingAlreadyCreated),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Obtained(5)),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Starting(6)),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Started("123456", "AndroidDevice")),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Creating(7)),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Waiting),
            Unit event RunTestCorelliumAndroid.InvokeDevices.Status(AndroidInstance.Event.Ready("123456")),
            Unit event RunTestCorelliumAndroid.ExecuteTests.Status(
                id = "123456",
                status = Instrument.Status(
                    code = 0,
                    startTime = 1,
                    endTime = 2,
                    details = Instrument.Status.Details(emptyMap(), "Class", "Test", null)
                )
            ),
            Unit event RunTestCorelliumAndroid.ExecuteTests.Error("1", Exception(), "path/to/log/1", 5..10),
            Unit event RunTestCorelliumAndroid.Created(File("path/to/apk.apk")),
            Unit event RunTestCorelliumAndroid.AlreadyExist(File("path/to/apk.apk")),
        )

        val printLog = format.output

        // ======================== WHEN ========================

        val nulls = events
            .onEach(printLog)
            .associateWith { format(it) }
            .filterValues { it == null }

        // ======================== THEN ========================

        Assert.assertTrue(
            nulls.keys.joinToString("\n", "Missing formatters for:\n"),
            nulls.isEmpty()
        )
    }
}
