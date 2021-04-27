package flank.corellium.adapter

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import flank.corellium.api.TestApk
import net.dongliu.apk.parser.ApkFile

object ParseTestApk : TestApk.Parse {
    override fun invoke(path: String) = TestApk(
        packageName = ApkFile(path).apkMeta.packageName,
        testCases = DexParser.findTestMethods(path).map(TestMethod::testName),
    )
}
