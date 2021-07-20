package flank.corellium.cli.test.android.task

import flank.apk.Apk
import flank.exection.parallel.type
import flank.exection.parallel.using

internal val apkApi = type<Apk.Api>() using { Apk.Api() }
