package flank.apk.internal

import flank.apk.Apk
import net.dongliu.apk.parser.ApkFile

internal val parseApkPackageName = Apk.ParsePackageName { path ->
    ApkFile(path).apkMeta.packageName
}
