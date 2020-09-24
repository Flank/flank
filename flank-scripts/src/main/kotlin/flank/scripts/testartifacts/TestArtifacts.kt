package flank.scripts.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class TestArtifactsCommand : CliktCommand(
    name = "testArtifacts"
) {
    init {
        subcommands(
            DownloadCommand(),
            UploadCommand()
        )
    }
    override fun run() {}
}

class DownloadCommand : CliktCommand(
    name = "download"
) {
    override fun run() {
        TODO()
    }
}


class UploadCommand : CliktCommand(
    name = "upload"
) {
    override fun run() {
        TODO()
    }
}

class PrepareCommand : CliktCommand(
    name = "prepare",
    help = "Creates fresh copy of master's test artifacts for current working branch"
) {
    override fun run() {
        TODO()
    }
}
