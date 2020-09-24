package flank.scripts.utils

import java.nio.file.Files
import java.nio.file.Paths

fun createSymbolicLink(
    link: String,
    target: String
) {
    Files.createSymbolicLink(
        Paths.get(link)
            .also { linkPath ->
                if (Files.isSymbolicLink(linkPath))
                    Files.delete(linkPath)
            }
            .toAbsolutePath().normalize(),

        Paths.get(target)
            .toAbsolutePath().normalize()
    )
}
