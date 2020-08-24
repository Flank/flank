package ftl.args.yml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import ftl.args.ArgsHelper.yamlMapper
import ftl.util.FlankGeneralError
import ftl.util.loadFile
import java.nio.file.Path

fun fixDevices(yamlPath: Path) =
    if (yamlPath.toFile().exists().not()) throw FlankGeneralError("Flank yml doesn't exist at path $yamlPath")
    else yamlPath.loadYml().fixDevices().saveFixedYml(yamlPath)

private fun Path.loadYml() = yamlMapper.readTree(loadFile(this))

private fun JsonNode.fixDevices() = also {
    getDevicesNode().getNotValidDevices().forEach { device ->
        device.fixDeviceVersion()
    }
}

internal fun JsonNode.getDevicesNode() = get(GCLOUD_NODE).get(DEVICES_NODE)

internal fun JsonNode.getNotValidDevices() = filter { it.deviceVersionValid().not() }

private fun JsonNode.deviceVersionValid() = get(VERSION_NODE).isTextual

private fun JsonNode.fixDeviceVersion() = (this as ObjectNode).put(VERSION_NODE, get(VERSION_NODE).asText())

private fun JsonNode.saveFixedYml(yamlPath: Path) = yamlMapper.writerWithDefaultPrettyPrinter().writeValue(yamlPath.toFile(), this)

const val MODEL_NODE = "model"
const val GCLOUD_NODE = "gcloud"
const val DEVICES_NODE = "device"
const val VERSION_NODE = "version"
