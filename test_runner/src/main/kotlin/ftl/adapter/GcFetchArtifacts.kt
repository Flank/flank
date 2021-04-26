package ftl.adapter

import ftl.adapter.google.fetchArtifacts
import ftl.api.Artifacts
import ftl.api.MatrixId

object GcFetchArtifacts : Artifacts.Fetch {
    override suspend fun invoke(input: Artifacts.Identity): Pair<MatrixId, List<String>> = fetchArtifacts(input)
}
