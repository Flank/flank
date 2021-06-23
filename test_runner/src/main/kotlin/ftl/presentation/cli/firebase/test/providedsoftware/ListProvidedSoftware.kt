package ftl.presentation.cli.firebase.test.providedsoftware

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.util.Alignment
import ftl.util.TableColumn
import ftl.util.buildTable

fun ProvidedSoftwareCatalog.toCliTable() = buildTable(
    TableColumn(
        ORCHESTRATOR_VERSION,
        listOf(orchestratorVersion),
        alignment = Alignment.LEFT
    )
)

private const val ORCHESTRATOR_VERSION = "ORCHESTRATOR VERSION"
