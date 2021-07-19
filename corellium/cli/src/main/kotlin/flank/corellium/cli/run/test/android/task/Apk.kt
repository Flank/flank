package flank.corellium.cli.run.test.android.task

import flank.apk.Apk
import flank.exection.parallel.type
import flank.exection.parallel.using

internal val apkApi = type<Apk.Api>() using { Apk.Api() }
