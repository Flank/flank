package ftl;

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.unit.TestContext

open class BaseTest {

   internal lateinit var vertx: Vertx
   internal lateinit var deploymentId: String
   internal lateinit var apiClient: ApiClient

   open fun before(context: TestContext) {
       vertx = Vertx.vertx(VertxOptions().setMaxEventLoopExecuteTime(java.lang.Long.MAX_VALUE))
       val async = context.async()
       vertx.deployVerticle("ftl.MainVerticle", { res ->
           if (res.succeeded()) {
               deploymentId = res.result()
               apiClient = ApiClient(vertx, "localhost", 8080)
           } else {
               res.cause().printStackTrace()
               context.fail("Verticle deployment failed!")
           }
           async.complete()
       })
   }

   open fun after(context: TestContext) {
       apiClient.close()
       vertx.close(context.asyncAssertSuccess())
   }
}