package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.JUnitTest
import ftl.client.junit.parseJUnit
import ftl.client.junit.parseLegacyJUnit
import java.io.File

object GoogleJUnitTestParse :
    JUnitTest.Result.ParseFromFiles,
    (File) -> JUnitTest.Result by {
        it.parseJUnit().toApiModel()
    }

object GoogleLegacyJunitTestParse :
    JUnitTest.Result.ParseFromFiles,
    (File) -> JUnitTest.Result by {
        it.parseLegacyJUnit().toApiModel()
    }
