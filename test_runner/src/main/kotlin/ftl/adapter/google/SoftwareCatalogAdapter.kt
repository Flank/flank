package ftl.adapter.google

import ftl.client.google.getProvidedSoftware
import ftl.client.google.providedSoftwareAsTable

internal fun getProvidedSoftwareToApiModel() = getProvidedSoftware()
internal fun getProvidedSoftwareAsATableToApiModel() = providedSoftwareAsTable()
