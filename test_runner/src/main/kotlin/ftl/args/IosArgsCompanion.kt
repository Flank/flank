package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.cli.firebase.test.ios.IosRunCommand
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.defaultIosConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.config.loadIosConfig
import ftl.config.plus
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

open class IosArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        ArgsHelper.mergeYmlMaps(
            CommonGcloudConfig,
            IosGcloudConfig,
            CommonFlankConfig,
            IosFlankConfig
        )
    }

    fun default() =
        createIosArgs(defaultIosConfig())

    fun load(yamlPath: Path, cli: IosRunCommand? = null) =
        load(loadFile(yamlPath), cli)

    @VisibleForTesting
    internal fun load(yamlReader: Reader, cli: IosRunCommand? = null) =
        createIosArgs(
            config = defaultIosConfig() +
                    loadIosConfig(reader = yamlReader) +
                    cli?.config
        ).apply {
            commonArgs.validate()
            this.validate()
        }
}
