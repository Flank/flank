package flank.shard

private const val LOWER_CASE_CHARS = "abcdefghijklmnopqrstuvwxyz"
private const val UPPER_CASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val ANDROID_TEST_METHOD_SEPARATOR = '#'
private const val ANDROID_PACKAGE_SEPARATOR = '.'
private const val IOS_TEST_METHOD_SEPARATOR = '/'

internal typealias ObfuscationMappings = MutableMap<String, MutableMap<String, String>>

internal fun ObfuscationMappings.obfuscateAndroidTestName(input: String): String {
    val obfuscatedPackageNameWithClass =
        obfuscateAndroidPackageAndClass(input.split(ANDROID_TEST_METHOD_SEPARATOR).first())

    return obfuscatedPackageNameWithClass + obfuscateAndroidMethodIfPresent(input, obfuscatedPackageNameWithClass)
}

private fun ObfuscationMappings.obfuscateAndroidPackageAndClass(packageNameWithClass: String) =
    packageNameWithClass
        .split(ANDROID_PACKAGE_SEPARATOR)
        .fold("") { previous, next ->
            val classChunk = getOrPut(previous) { linkedMapOf() }
            val obfuscatedPart = classChunk.getOrPut(next) { nextSymbol(next, classChunk) }
            if (previous.isBlank()) obfuscatedPart else "$previous$ANDROID_PACKAGE_SEPARATOR$obfuscatedPart"
        }

private fun ObfuscationMappings.obfuscateAndroidMethodIfPresent(
    input: String,
    obfuscatedPackageNameWithClass: String
) = if (input.contains(ANDROID_TEST_METHOD_SEPARATOR))
    ANDROID_TEST_METHOD_SEPARATOR + obfuscateMethodName(
        methodName = input.split(ANDROID_TEST_METHOD_SEPARATOR).last(),
        context = getOrPut(obfuscatedPackageNameWithClass) { mutableMapOf() }
    )
else ""

internal fun ObfuscationMappings.obfuscateIosTestName(input: String): String {
    val className = input.split(IOS_TEST_METHOD_SEPARATOR).first()
    val obfuscatedClassName = getOrPut("") { mutableMapOf() }.run {
        getOrPut(className) { nextSymbol(className, this) }
    }
    return obfuscatedClassName +
        IOS_TEST_METHOD_SEPARATOR +
        obfuscateMethodName(
            methodName = input.split(IOS_TEST_METHOD_SEPARATOR).last(),
            context = getOrPut(obfuscatedClassName) { linkedMapOf() }
        )
}

private fun nextSymbol(key: String, context: Map<String, String>): String {
    val isLowerCaseKey = key.first().isLowerCase()
    val possibleSymbols = if (isLowerCaseKey) LOWER_CASE_CHARS else UPPER_CASE_CHARS

    val currentContextItemCount = context.values.count {
        if (isLowerCaseKey) it.first().isLowerCase() else it.first().isUpperCase()
    }
    val repeatSymbol = currentContextItemCount / possibleSymbols.length + 1

    return possibleSymbols[currentContextItemCount % possibleSymbols.length].toString().repeat(repeatSymbol)
}

private fun obfuscateMethodName(methodName: String, context: MutableMap<String, String>) =
    context.getOrPut(methodName) { nextSymbol(methodName, context) }
