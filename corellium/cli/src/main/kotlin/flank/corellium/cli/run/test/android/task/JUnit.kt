package flank.corellium.cli.run.test.android.task

import flank.exection.parallel.type
import flank.exection.parallel.using
import flank.junit.JUnit

val jUnitApi = type<JUnit.Api>() using { JUnit.Api() }
