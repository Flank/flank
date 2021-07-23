package flank.corellium.cli.test.android

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.cli.TestAndroidCommand
import flank.corellium.domain.TestAndroid
import flank.exection.parallel.type
import flank.instrument.log.Instrument
import flank.log.Event
import flank.log.buildFormatter

internal val format = buildFormatter<String> {

    Event.Start(TestAndroidCommand.Config) { "* Loading configuration" }
    Event.Start(TestAndroid.Args) { "* Preparing arguments" }
    Event.Start(type<CorelliumApi>()) { "* Resolving Corellium API" }
    Event.Start(TestAndroid.Authorize) { "* Authorizing" }
    Event.Start(TestAndroid.Authorize) { "* Authorizing" }
    Event.Start(TestAndroid.CleanUp) { "* Cleaning instances" }
    Event.Start(TestAndroid.OutputDir) { "* Preparing output directory" }
    Event.Start(TestAndroid.DumpShards) { "* Dumping shards" }
    Event.Start(TestAndroid.ExecuteTests) { "* Executing tests" }
    Event.Start(TestAndroid.CompleteTests) { "* Finish" }
    Event.Start(TestAndroid.GenerateReport) { "* Generating report" }
    Event.Start(TestAndroid.InvokeDevices) { "* Invoking devices" }
    Event.Start(TestAndroid.LoadPreviousDurations) { "* Obtaining previous test cases durations" }
    Event.Start(TestAndroid.ParseApkInfo) { "* Parsing apk info" }
    Event.Start(TestAndroid.ParseTestCases) { "* Parsing test cases" }
    Event.Start(TestAndroid.PrepareShards) { "* Calculating shards" }

    TestAndroid.LoadPreviousDurations.Searching { "Searching in $this JUnitReport.xml files..." }
    TestAndroid.LoadPreviousDurations.Summary::class { "For $required test cases, found $matching matching and $unknown unknown" }
    TestAndroid.InstallApks.Status {
        when (this) {
            is AndroidApps.Event.Connecting.Agent -> "$instanceId: Connecting agent"
            is AndroidApps.Event.Connecting.Console -> "$instanceId: Connecting console"
            is AndroidApps.Event.Apk.Uploading -> "$instanceId: Uploading apk $path"
            is AndroidApps.Event.Apk.Installing -> "$instanceId: Installing apk $path"
        }
    }
    TestAndroid.InvokeDevices.Status {
        when (this) {
            is AndroidInstance.Event.GettingAlreadyCreated -> "Getting instances already created by flank."
            is AndroidInstance.Event.Obtained -> "Obtained $size already created devices"
            is AndroidInstance.Event.Starting -> "Starting not running $size instances."
            is AndroidInstance.Event.Started -> "$id - $name"
            is AndroidInstance.Event.Creating -> "Creating additional $size instances. Connecting to the agents may take longer."
            is AndroidInstance.Event.Waiting -> "Wait until all instances are ready..."
            is AndroidInstance.Event.Ready -> "ready: $id"
            is AndroidInstance.Event.AllReady -> "All instances invoked and ready to use."
        }
    }
    TestAndroid.ExecuteTests.Plan {
        instances.toList().joinToString("\n") { (id, commands) ->
            "$id:\n" + commands.joinToString("\n") { " - $it" }
        }
    }
    TestAndroid.ExecuteTests.Result::class {
        when (val status = status) {
            is Instrument.Status -> "$id: " + status.details.run { "$className#$testName" } + " - " + status.code
            else -> null
        }
    }
    TestAndroid.ExecuteTests.Error::class {
        """
            Error while parsing results from instance $id.
            For details check $logFile lines $lines.
            
        """.trimIndent() + cause.stackTraceToString()
    }
    TestAndroid.Created { "Created $path" }
    TestAndroid.AlreadyExist { "Already exist $path" }

    match { it as? String } to { this }
}
