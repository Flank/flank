package flank.corellium.adapter

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import flank.corellium.api.Apk
import net.dongliu.apk.parser.ApkFile
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

val parseApkTestCases = Apk.ParseTestCases { path ->
    DexParser.findTestMethods(path)
        .map(TestMethod::testName)
}

val parseApkPackageName = Apk.ParsePackageName { path ->
    ApkFile(path).apkMeta.packageName
}

val parseApkInfo = Apk.ParseInfo { path ->
    Apk.Info(
        packageName = ApkFile(path).apkMeta.packageName,
        testRunner = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(InputSource(StringReader(ApkFile(path).manifestXml)))
            .getElementsByTagName("instrumentation").item(0).attributes
            .getNamedItem("android:name").nodeValue
    )
}
