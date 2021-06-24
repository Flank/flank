package ftl.presentation.cli.firebase.test.providedsoftware

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.util.Align
import ftl.util.TableColumn
import ftl.util.buildTable

fun ProvidedSoftwareCatalog.toCliTable() = buildTable(
    TableColumn(
        ORCHESTRATOR_VERSION,
        listOf(orchestratorVersion),
        align = Align.LEFT
    )
)

private const val ORCHESTRATOR_VERSION = "ORCHESTRATOR VERSION"
