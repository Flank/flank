@file:JvmName("AndroidExampleNoVPN")

package flank.corellium.sandbox.android

import flank.corellium.client.agent.disconnect
import flank.corellium.client.agent.uploadFile
import flank.corellium.client.console.close
import flank.corellium.client.console.launchOutputPrinter
import flank.corellium.client.console.sendCommand
import flank.corellium.client.console.waitForIdle
import flank.corellium.client.core.connectAgent
import flank.corellium.client.core.connectConsole
import flank.corellium.client.core.connectCorellium
import flank.corellium.client.core.createNewInstance
import flank.corellium.client.core.getAllProjects
import flank.corellium.client.core.getInstanceInfo
import flank.corellium.client.core.getProjectInstancesList
import flank.corellium.client.core.waitUntilInstanceIsReady
import flank.corellium.client.data.Instance
import flank.corellium.client.data.Instance.BootOptions
import flank.corellium.sandbox.config.Config
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

private const val instanceName = "flank-android-20"
private const val flavor = "ranchu"
private const val os = "11.0.0"
private const val screen = "720x1280:280"
private const val projectName = "Default Project"
private const val apkPath = "./test_artifacts/master/apk/app-debug.apk"
private const val testApkPath = "./test_artifacts/master/apk/app-multiple-success-debug-androidTest.apk"
private const val pathToUpload = "/sdcard"

fun main(): Unit = runBlocking {
    val client = connectCorellium(
        api = Config.api,
        username = Config.username,
        password = Config.password
    )

    val projectId = client.getAllProjects().first { it.name == projectName }.id

    println("Looking for $instanceName instance")
    val instanceId = client.getProjectInstancesList(projectId)
        .find { it.name == instanceName }?.id
        ?: run {
            println("Instance not found. Creating new one")
            client.createNewInstance(
                Instance(
                    project = projectId,
                    name = instanceName,
                    flavor = flavor,
                    os = os,
                    bootOptions = BootOptions(
                        screen = screen
                    )
                )
            )
        }

    println("Wait until instance is ready (may take some time ~3-5 min)")
    client.waitUntilInstanceIsReady(instanceId)

    val instance = client.getInstanceInfo(instanceId)

    println("Creating agent")
    println("Await agent is connected and ready to use")
    val agent = client.connectAgent(instance.agent?.info ?: error("Agent info is not present"))
    println("Agent ready")

    agent.uploadFile(
        path = "$pathToUpload/app-debug.apk",
        bytes = File(apkPath).readBytes()
    )
    println("App apk uploaded")

    agent.uploadFile(
        path = "$pathToUpload/app-multiple-success-debug-androidTest.apk",
        bytes = File(testApkPath).readBytes()
    )
    println("Test apk uploaded")

    val console = client.connectConsole(instanceId)
    println("Console connected")

    // prevent flooding the output by the system and kernel logging
    console.sendCommand("su")
    console.sendCommand("dmesg -n 1")
    console.sendCommand("exit")

    println("Installing apps...")
    console.sendCommand("pm install $pathToUpload/app-debug.apk")
    console.sendCommand("pm install -t $pathToUpload/app-multiple-success-debug-androidTest.apk")

    println("Running tests... ")
    console.sendCommand("am instrument -r -w com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner")
    console.launchOutputPrinter()

    console.waitForIdle(5_000)
    println()
    println("Console idle up 10s")

    println("Removing apps...")
    console.sendCommand("pm uninstall com.example.test_app")
    delay(2000)

    println("Disconnecting...")
    console.close()

    agent.disconnect()
}
