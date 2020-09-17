package flank.scripts.dependencies.update

data class DependencyUpdate(
    val name: String,
    val valName: String,
    val oldVersion: String,
    val newVersion: String
)
