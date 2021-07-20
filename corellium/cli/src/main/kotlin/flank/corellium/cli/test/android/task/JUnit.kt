package flank.corellium.cli.test.android.task

import flank.exection.parallel.type
import flank.exection.parallel.using
import flank.junit.JUnit

internal val jUnitApi = type<JUnit.Api>() using { JUnit.Api() }
