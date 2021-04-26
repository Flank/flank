package flank.corellium.adapter

import flank.corellium.api.AndroidApps
import flank.corellium.client.agent.uploadFile
import flank.corellium.client.console.sendCommand
import flank.corellium.client.core.connectAgent
import flank.corellium.client.core.connectConsole
import flank.corellium.client.core.getAllProjects
import flank.corellium.client.core.getProjectInstancesList
import java.io.File

private const val PATH_TO_UPLOAD = "/sdcard"

class InstallAndroidApps(
    private val projectName: String
) : AndroidApps.Install {

    override suspend fun List<AndroidApps>.invoke() {
        val corellium = corellium
        val projectId = corellium.getAllProjects().first { it.name == projectName }.id
        val instances = corellium.getProjectInstancesList(projectId).associateBy { it.id }

        forEach { apps ->
            val instance = instances.getValue(apps.instanceId)
            val agent = corellium.connectAgent(instance.agent!!.info)
            val console = corellium.connectConsole(instance.id)

            apps.paths.forEach { localPath ->
                val file = File(localPath)
                val remotePath = "$PATH_TO_UPLOAD/${file.name}"

                agent.uploadFile(remotePath, file.readBytes())
                console.sendCommand("pm install $remotePath")
            }
        }
    }
}
