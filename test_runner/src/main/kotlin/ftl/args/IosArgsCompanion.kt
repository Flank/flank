package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.args.yml.mergeYmlKeys
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.defaultIosConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.config.loadIosConfig
import ftl.config.plus
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path

open class IosArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        mergeYmlKeys(
            CommonGcloudConfig,
            IosGcloudConfig,
            CommonFlankConfig,
            IosFlankConfig
        )
    }

    fun loadOrDefault(yamlPath: Path, cli: IosRunCommand? = null) =
        if (Files.exists(yamlPath)) load(yamlPath, cli)
        else default()

    fun load(yamlPath: Path, cli: IosRunCommand? = null) =
        load(loadFile(yamlPath), cli)

    fun default() = createIosArgs(defaultIosConfig())

    @VisibleForTesting
    internal fun load(yamlReader: Reader, cli: IosRunCommand? = null) =
        createIosArgs(
            config = defaultIosConfig() +
                loadIosConfig(reader = yamlReader) +
                cli?.config,
            obfuscate = cli?.obfuscate ?: false
        )
}
