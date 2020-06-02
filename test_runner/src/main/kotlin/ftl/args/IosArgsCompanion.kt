package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.cli.firebase.test.ios.IosRunCommand
import ftl.config.defaultIosConfig
import ftl.config.loadIosConfig
import ftl.config.plus
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

open class IosArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        ArgsHelper.mergeYmlMaps(
            GcloudYml,
            IosGcloudYml,
            FlankYml,
            IosFlankYml
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
