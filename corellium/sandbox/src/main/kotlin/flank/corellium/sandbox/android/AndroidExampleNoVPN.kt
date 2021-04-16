@file:JvmName("AndroidExampleNoVPN")

package flank.corellium.sandbox.android

import flank.corellium.client.agent.disconnect
import flank.corellium.client.agent.uploadFile
import flank.corellium.client.console.close
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
import kotlinx.coroutines.runBlocking
import java.io.File

private const val instanceName = "corellium-android"
private const val flavor = "ranchu"
private const val os = "11.0.0"
private const val screen = "720x1280:280"
private const val projectName = "Default Project"
private const val apkPath = "./corellium/corellium-sandbox/src/main/resources/android/app-debug.apk"
private const val testApkPath =
    "./corellium/corellium-sandbox/src/main/resources/android/app-multiple-success-debug-androidTest.apk"
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

    agent.uploadFile(
        path = "$pathToUpload/app-multiple-success-debug-androidTest.apk",
        bytes = File(testApkPath).readBytes()
    )

    val console = client.connectConsole(instanceId)

    console.sendCommand("pm install $pathToUpload/app-debug.apk")
    console.sendCommand("pm install -t $pathToUpload/app-multiple-success-debug-androidTest.apk")
    console.sendCommand("am instrument -w com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner")

    console.waitForIdle(5_000)
    console.close()

    agent.disconnect()
}
