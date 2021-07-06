package flank.apk.internal

import flank.apk.Apk
import net.dongliu.apk.parser.ApkFile
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

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
