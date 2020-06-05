package ftl.util

private const val LOWER_CASE_CHARS = "abcdefghijklmnopqrstuvwxyz"
private const val UPPER_CASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val START_CONTEXT_KEY = ""

interface Obfuscation {
    fun obfuscate(input: String): String
}

class AndroidObfuscation : Obfuscation {
    private val contextMapAndroid = mutableMapOf<String, MutableMap<String, String>>()
    private val androidTestMethodSeparator = '#'
    private val androidPackageSeparator = '.'

    override fun obfuscate(input: String): String {
        val obfuscatedPackageNameWithClass =
            obfuscateAndroidPackageAndClass(input.split(androidTestMethodSeparator).first())

        val obfuscatedMethodName = obfuscateMethodName(
            methodName = input.split(androidTestMethodSeparator).last(),
            context = contextMapAndroid.getOrPut(obfuscatedPackageNameWithClass) { mutableMapOf() }
        )

        return "$obfuscatedPackageNameWithClass$androidTestMethodSeparator$obfuscatedMethodName"
    }

    private fun obfuscateAndroidPackageAndClass(packageNameWithClass: String): String {
        return packageNameWithClass
            .split(androidPackageSeparator)
            .fold(START_CONTEXT_KEY) { previous, next ->
                val classChunk = contextMapAndroid.getOrPut(previous) { linkedMapOf() }
                val obfuscatedPart = classChunk.getOrPut(next) { nextSymbol(next, classChunk) }
                if (previous.isEmpty()) obfuscatedPart else "$previous$androidPackageSeparator$obfuscatedPart"
            }
    }
}

class IosObfuscation : Obfuscation {
    private val contextMapIos = mutableMapOf<String, MutableMap<String, String>>()
    private val iOsTestMethodSeparator = '/'

    override fun obfuscate(input: String): String {
        val className = input.split(iOsTestMethodSeparator).first()
        val obfuscatedClassName = contextMapIos.getOrPut(START_CONTEXT_KEY) { mutableMapOf() }.run {
            getOrPut(className) { nextSymbol(className, this) }
        }
        return obfuscatedClassName +
                iOsTestMethodSeparator +
                obfuscateMethodName(
                    methodName = input.split(iOsTestMethodSeparator).last(),
                    context = contextMapIos.getOrPut(obfuscatedClassName) { linkedMapOf() }
                )
    }
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

private fun obfuscateMethodName(methodName: String, context: MutableMap<String, String>): String {
    return context.getOrPut(methodName) { nextSymbol(methodName, context) }
}
