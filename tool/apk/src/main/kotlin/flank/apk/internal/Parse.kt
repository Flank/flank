package flank.apk.internal

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import flank.apk.Apk
import net.dongliu.apk.parser.ApkFile
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

internal val parseApkTestCases = Apk.ParseTestCases { path ->
    DexParser.findTestMethods(path).map(TestMethod::testName)
}

internal val parseApkPackageName = Apk.ParsePackageName { path ->
    ApkFile(path).apkMeta.packageName
}

internal val parseApkInfo = Apk.ParseInfo { path ->
    val apkFile = ApkFile(path)
    Apk.Info(
        packageName = apkFile.apkMeta.packageName,
        testRunner = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(InputSource(StringReader(apkFile.manifestXml)))
            .getElementsByTagName("instrumentation").item(0).attributes
            .getNamedItem("android:name").nodeValue
    )
}
