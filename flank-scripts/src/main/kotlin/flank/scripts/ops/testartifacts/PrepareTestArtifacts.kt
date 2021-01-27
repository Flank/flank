package flank.scripts.ops.testartifacts

fun Context.prepareTestArtifacts(src: String = "master") {
    print("* Creating artifacts directory for $branch based on $src - ")
    projectRoot.testArtifacts.apply {
        resolve(branch).let { dstDir ->
            check(!dstDir.exists()) { "${dstDir.absolutePath} already exist." }
            resolve(src).copyRecursively(dstDir)
        }
    }
    println("OK")
}
