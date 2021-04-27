package ftl.adapter

import ftl.adapter.google.fetchArtifacts
import ftl.api.Artifacts

object GcFetchArtifacts :
    Artifacts.Fetch,
    (Artifacts.Identity) -> Pair<String, List<String>> by { identity ->
        fetchArtifacts(identity)
    }
