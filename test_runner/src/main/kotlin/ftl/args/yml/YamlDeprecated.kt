package ftl.args.yml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import ftl.args.ArgsHelper.yamlMapper
import ftl.util.Utils
import ftl.util.Utils.fatalError
import java.nio.file.Files
import java.nio.file.Path

object YamlDeprecated {

    @Suppress("EnumEntryName", "EnumNaming")
    enum class Parent { gcloud, flank }

    enum class Level { Warning, Error }

    private data class Key(
        val parent: Parent,
        val name: String
    )

    private data class ModifiedKey(
        val old: Key,
        val new: Key,
        val level: Level
    )

    private val transforms = listOf(
        // flank: testShards -> flank: maxTestShards
        ModifiedKey(
            Key(YamlDeprecated.Parent.flank, "testShards"),
            Key(YamlDeprecated.Parent.flank, "maxTestShards"),
            Level.Warning
        )
    )

    private data class Transform(
        val keyValue: JsonNode,
        val key: ModifiedKey
    )

    private fun JsonNode.remove(parent: Parent, child: String) {
        (this[parent.toString()] as ObjectNode).remove(child)
    }

    private fun JsonNode.replace(parent: Parent, child: String, value: JsonNode) {
        (this[parent.toString()] as ObjectNode).replace(child, value)
    }

    private fun mutate(parsed: JsonNode, changes: List<Transform>) {
        changes.forEach { transform ->
            mutateNode(parsed, transform.keyValue, transform.key.old, transform.key.new)
        }
    }

    private fun mutateNode(parsed: JsonNode, keyValue: JsonNode, old: Key, new: Key) {
        parsed.remove(parent = old.parent, child = old.name)
        parsed.replace(parent = new.parent, child = new.name, value = keyValue)
    }

    private fun validate(key: Key, keyValue: JsonNode): Transform? {
        transforms.forEach {
            if (it.old == key) {
                println("${it.level}: `${it.old.parent}: ${it.old.name}:` renamed to `${it.new.parent}: ${it.new.name}:`")
                return Transform(keyValue, it)
            }
        }

        return null
    }

    private val yamlWriter by lazy { yamlMapper.writerWithDefaultPrettyPrinter() }

    fun modify(yamlPath: Path, fix: Boolean = false): Boolean {
        if (yamlPath.toFile().exists().not()) fatalError("Flank yml doesn't exist at path $yamlPath")
        val data = String(Files.readAllBytes(yamlPath))

        val (errorDetected, string) = modify(data)

        if (fix) {
            Files.write(yamlPath, string.toByteArray())
            println("\nUpdated flank.yml file")
        }
        return errorDetected
    }

    // Throw exception when Level.Error modified key is found.
    fun modifyAndThrow(yamlData: String, android: Boolean): String {
        val (error, data) = YamlDeprecated.modify(yamlData)

        if (error) {
            val platform = if (android) "android" else "ios"
            Utils.fatalError("Invalid keys detected! Auto fix with: flank $platform doctor --fix")
        }

        return data
    }

    fun modify(yamlData: String): Pair<Boolean, String> {
        val parsed = yamlMapper.readTree(yamlData)
        yamlMapper.writerWithDefaultPrettyPrinter()
        var errorDetected = false
        val changes = mutableListOf<Transform>()

        listOf("gcloud", "flank").forEach { keyType ->
            parsed[keyType]?.fields()?.forEach { (keyName, keyValue) ->
                val type = Parent.valueOf(keyType)
                val newChange = validate(Key(type, keyName), keyValue)

                if (newChange != null) {
                    changes.add(newChange)
                    if (newChange.key.level == Level.Error) errorDetected = true
                }
            }
        }

        mutate(parsed, changes)

        return errorDetected to yamlWriter.writeValueAsString(parsed)
    }
}
