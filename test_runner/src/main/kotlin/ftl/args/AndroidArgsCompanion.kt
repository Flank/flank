package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.args.yml.mergeYmlKeys
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.defaultAndroidConfig
import ftl.config.loadAndroidConfig
import ftl.config.plus
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path

open class AndroidArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        mergeYmlKeys(
            CommonGcloudConfig,
            AndroidGcloudConfig,
            CommonFlankConfig,
            AndroidFlankConfig
        )
    }

    fun loadOrDefault(yamlPath: Path, cli: AndroidRunCommand? = null) =
        if (Files.exists(yamlPath)) load(yamlPath, cli)
        else default()

    fun load(yamlPath: Path, cli: AndroidRunCommand? = null) =
        load(loadFile(yamlPath), cli)

    fun default() = createAndroidArgs(defaultAndroidConfig())

    @VisibleForTesting
    internal fun load(yamlReader: Reader, cli: AndroidRunCommand? = null) =
        createAndroidArgs(
            config = defaultAndroidConfig() +
                loadAndroidConfig(reader = yamlReader) +
                cli?.config,
            obfuscate = cli?.obfuscate ?: false
        )
}
