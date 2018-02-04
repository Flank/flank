package ftl;

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.streams.ReadStream
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * CreateV1ProjectsTestMatricesByProjectId Test
 */
@RunWith(VertxUnitRunner::class)
class CreateV1ProjectsTestMatricesByProjectIdTest : BaseTest() {

    @Before
    override fun before(context: TestContext) {
        super.before(context)
        //TODO add some test initialization code like security token retrieval
    }

    @After
    override fun after(context: TestContext) {
        //TODO add some test end code like session destroy
        super.after(context)
    }

    @Test
    fun test200(test: TestContext) {
        val async = test.async(2);
        var projectId: String? = null;
        var uploadProtocol: String? = null;
        var prettyPrint: Boolean? = null;
        var uploadType: String? = null;
        var fields: String? = null;
        var $Xgafv: String? = null;
        var callback: String? = null;
        var alt: String? = null;
        var accessToken: String? = null;
        var key: String? = null;
        var quotaUser: String? = null;
        var pp: Boolean? = null;
        var oauthToken: String? = null;
        var bearerToken: String? = null;
        var requestId: String? = null;

        // TODO set parameters for CreateV1ProjectsTestMatricesByProjectIdWithEmptyBody request
        projectId = null;
        uploadProtocol = null;
        prettyPrint = null;
        uploadType = null;
        fields = null;
        $Xgafv = null;
        callback = null;
        alt = null;
        accessToken = null;
        key = null;
        quotaUser = null;
        pp = null;
        oauthToken = null;
        bearerToken = null;
        requestId = null;
        apiClient.CreateV1ProjectsTestMatricesByProjectIdWithEmptyBody(projectId, uploadProtocol, prettyPrint, uploadType, fields, $Xgafv, callback, alt, accessToken, key, quotaUser, pp, oauthToken, bearerToken, requestId, Handler { ar ->
            if (ar.succeeded()) {
                test.assertEquals(200, ar.result().statusCode());
                //TODO add your asserts
            } else {
                ar.cause().printStackTrace();
                test.fail("Request CreateV1ProjectsTestMatricesByProjectIdWithEmptyBody failed");
            }
            async.countDown();
        });

        // TODO set parameters for CreateV1ProjectsTestMatricesByProjectIdWithJson request
        projectId = null;
        uploadProtocol = null;
        prettyPrint = null;
        uploadType = null;
        fields = null;
        $Xgafv = null;
        callback = null;
        alt = null;
        accessToken = null;
        key = null;
        quotaUser = null;
        pp = null;
        oauthToken = null;
        bearerToken = null;
        requestId = null;
        var body = JsonObject();
        apiClient.CreateV1ProjectsTestMatricesByProjectIdWithJson(projectId, uploadProtocol, prettyPrint, uploadType, fields, $Xgafv, callback, alt, accessToken, key, quotaUser, pp, oauthToken, bearerToken, requestId, body, Handler { ar ->
            if (ar.succeeded()) {
                test.assertEquals(200, ar.result().statusCode());
                //TODO add your asserts
            } else {
                ar.cause().printStackTrace();
                test.fail("Request CreateV1ProjectsTestMatricesByProjectIdWithJson failed");
            }
            async.countDown();
        });
    }


}