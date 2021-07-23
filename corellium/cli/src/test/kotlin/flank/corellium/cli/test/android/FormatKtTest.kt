package flank.corellium.cli.test.android

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.domain.TestAndroid
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
            TestAndroid.Authorize event Event.Start,
            TestAndroid.CleanUp event Event.Start,
            TestAndroid.OutputDir event Event.Start,
            TestAndroid.DumpShards event Event.Start,
            TestAndroid.ExecuteTests event Event.Start,
            TestAndroid.CompleteTests event Event.Start,
            TestAndroid.GenerateReport event Event.Start,
            TestAndroid.InvokeDevices event Event.Start,
            TestAndroid.LoadPreviousDurations event Event.Start,
            TestAndroid.ParseApkInfo event Event.Start,
            TestAndroid.ParseTestCases event Event.Start,
            TestAndroid.PrepareShards event Event.Start,
            Unit event TestAndroid.LoadPreviousDurations.Searching(5),
            Unit event TestAndroid.LoadPreviousDurations.Summary(1, 2, 3),
            Unit event TestAndroid.InstallApks.Status(AndroidApps.Event.Connecting.Agent("123456")),
            Unit event TestAndroid.InstallApks.Status(AndroidApps.Event.Connecting.Console("123456")),
            Unit event TestAndroid.InstallApks.Status(AndroidApps.Event.Apk.Uploading("123456", "path/to/apk.apk")),
            Unit event TestAndroid.InstallApks.Status(AndroidApps.Event.Apk.Installing("123456", "path/to/apk.apk")),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.GettingAlreadyCreated),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Obtained(5)),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Starting(6)),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Started("123456", "AndroidDevice")),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Creating(7)),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Waiting),
            Unit event TestAndroid.InvokeDevices.Status(AndroidInstance.Event.Ready("123456")),
            Unit event TestAndroid.ExecuteTests.Result(
                id = "123456",
                status = Instrument.Status(
                    code = 0,
                    startTime = 1,
                    endTime = 2,
                    details = Instrument.Status.Details(emptyMap(), "Class", "Test", null)
                )
            ),
            Unit event TestAndroid.ExecuteTests.Error("1", Exception(), "path/to/log/1", 5..10),
            Unit event TestAndroid.Created(File("path/to/apk.apk")),
            Unit event TestAndroid.AlreadyExist(File("path/to/apk.apk")),
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
