package flank.corellium.cli.run.test.android

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.cli.RunTestCorelliumAndroidCommand
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.exection.parallel.type
import flank.instrument.log.Instrument
import flank.log.Event
import flank.log.buildFormatter

internal val format = buildFormatter<String> {

    Event.Start(RunTestCorelliumAndroidCommand.Config) { "* Loading configuration" }
    Event.Start(RunTestCorelliumAndroid.Args) { "* Preparing arguments" }
    Event.Start(type<CorelliumApi>()) { "* Resolving Corellium API" }
    Event.Start(RunTestCorelliumAndroid.Authorize) { "* Authorizing" }
    Event.Start(RunTestCorelliumAndroid.Authorize) { "* Authorizing" }
    Event.Start(RunTestCorelliumAndroid.CleanUp) { "* Cleaning instances" }
    Event.Start(RunTestCorelliumAndroid.OutputDir) { "* Preparing output directory" }
    Event.Start(RunTestCorelliumAndroid.DumpShards) { "* Dumping shards" }
    Event.Start(RunTestCorelliumAndroid.ExecuteTests) { "* Executing tests" }
    Event.Start(RunTestCorelliumAndroid.CompleteTests) { "* Finish" }
    Event.Start(RunTestCorelliumAndroid.GenerateReport) { "* Generating report" }
    Event.Start(RunTestCorelliumAndroid.InstallApks) { "* Installing apks" }
    Event.Start(RunTestCorelliumAndroid.InvokeDevices) { "* Invoking devices" }
    Event.Start(RunTestCorelliumAndroid.LoadPreviousDurations) { "* Obtaining previous test cases durations" }
    Event.Start(RunTestCorelliumAndroid.ParseApkInfo) { "* Parsing apk info" }
    Event.Start(RunTestCorelliumAndroid.ParseTestCases) { "* Parsing test cases" }
    Event.Start(RunTestCorelliumAndroid.PrepareShards) { "* Calculating shards" }

    RunTestCorelliumAndroid.LoadPreviousDurations.Searching { "Searching in $this JUnitReport.xml files..." }
    RunTestCorelliumAndroid.LoadPreviousDurations.Summary::class { "For $required test cases, found $matching matching and $unknown unknown" }
    RunTestCorelliumAndroid.InstallApks.Status {
        when (this) {
            is AndroidApps.Event.Connecting.Agent -> "$instanceId: Connecting agent"
            is AndroidApps.Event.Connecting.Console -> "$instanceId: Connecting console"
            is AndroidApps.Event.Apk.Uploading -> "$instanceId: Uploading apk $path"
            is AndroidApps.Event.Apk.Installing -> "$instanceId: Installing apk $path"
        }
    }
    RunTestCorelliumAndroid.InvokeDevices.Status {
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
    RunTestCorelliumAndroid.ExecuteTests.Plan {
        instances.toList().joinToString("\n") { (id, commands) ->
            "$id:\n" + commands.joinToString("\n") { " - $it" }
        }
    }
    RunTestCorelliumAndroid.ExecuteTests.Status::class {
        when (val status = status) {
            is Instrument.Status -> "$id: " + status.details.run { "$className#$testName" } + " - " + status.code
            else -> null
        }
    }
    RunTestCorelliumAndroid.ExecuteTests.Error::class {
        """
            Error while parsing results from instance $id.
            For details check $logFile lines $lines.
            
        """.trimIndent() + cause.stackTraceToString()
    }
    RunTestCorelliumAndroid.Created { "Created $path" }
    RunTestCorelliumAndroid.AlreadyExist { "Already exist $path" }

    match { it as? String } to { this }
}
