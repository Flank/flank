package flank.corellium.adapter

import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidApps.Event.Apk
import flank.corellium.api.AndroidApps.Event.Connecting
import flank.corellium.client.agent.disconnect
import flank.corellium.client.agent.uploadFile
import flank.corellium.client.console.close
import flank.corellium.client.console.sendCommand
import flank.corellium.client.core.connectAgent
import flank.corellium.client.core.connectConsole
import flank.corellium.client.core.getAllProjects
import flank.corellium.client.core.getProjectInstancesList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.File

private const val PATH_TO_UPLOAD = "/sdcard"

fun installAndroidApps(
    projectName: String
) = AndroidApps.Install { apps ->
    channelFlow<AndroidApps.Event> {
        install(projectName, apps).join()
    }
}

private suspend fun SendChannel<AndroidApps.Event>.install(
    projectName: String,
    appsList: List<AndroidApps>
): Job =
    corellium.launch {
        val projectId = corellium.getAllProjects().first { it.name == projectName }.id
        val instances = corellium.getProjectInstancesList(projectId).associateBy { it.id }

        appsList.forEach { apps ->
            launch(Dispatchers.IO) {

                val instance = instances.getValue(apps.instanceId)

                send(Connecting.Agent(instance.id))

                val agentInfo = requireNotNull(instance.agent?.info) {
                    "Cannot connect to the agent, no agent info for instance ${instance.name} with id: ${instance.id}"
                }
                val agent = corellium.connectAgent(agentInfo)

                send(Connecting.Console(instance.id))
                val console = corellium.connectConsole(instance.id)

                // Disable system logging
                flowOf("su", "dmesg -n 1", "exit").collect(console::sendCommand)

                apps.paths.forEach { localPath ->
                    val file = File(localPath)
                    val remotePath = "$PATH_TO_UPLOAD/${file.name}"

                    send(Apk.Uploading(instance.id, localPath))
                    agent.uploadFile(remotePath, file.readBytes())

                    send(Apk.Installing(instance.id, localPath))
                    console.sendCommand(
                        // Current solution is enough for the MVP.
                        // Fixme: Find better solution for recognizing test apk.
                        if (localPath.endsWith("androidTest.apk"))
                            "pm install -t $remotePath" else
                            "pm install $remotePath"
                    )
                }

                console.close()
                agent.disconnect()
            }
        }
    }
