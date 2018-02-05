package ftl;

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.Router
import io.vertx.core.Future

class MainVerticle : AbstractVerticle() {

    var server: HttpServer? = null

    override fun start(future: Future<Void>) {
        OpenAPI3RouterFactory.createRouterFactoryFromFile(vertx, MainVerticle::class.java.getResource("/testing_v1_openapi_3.json").getFile(), { openAPI3RouterFactoryAsyncResult ->
            if (openAPI3RouterFactoryAsyncResult.succeeded()) {
                var routerFactory: OpenAPI3RouterFactory = openAPI3RouterFactoryAsyncResult.result();

                // Enable automatic response when ValidationException is thrown
                routerFactory.enableValidationFailureHandler(true);

                // Add routes handlers
                routerFactory.addHandlerByOperationId("GetV1TestEnvironmentCatalog", ftl.handlers.GetV1TestEnvironmentCatalogHandler());
                routerFactory.addHandlerByOperationId("CancelV1ProjectsTestMatricesCancelByProjectId", ftl.handlers.CancelV1ProjectsTestMatricesCancelByProjectIdHandler());
                routerFactory.addHandlerByOperationId("GetV1ProjectsTestMatricesByProjectId", ftl.handlers.GetV1ProjectsTestMatricesByProjectIdHandler());
                routerFactory.addHandlerByOperationId("CreateV1ProjectsTestMatricesByProjectId", ftl.handlers.CreateV1ProjectsTestMatricesByProjectIdHandler());


                // Generate the router
                var router : Router = routerFactory.getRouter();
                server = vertx.createHttpServer(HttpServerOptions().setPort(8080).setHost("localhost"));
                server?.requestHandler(router::accept)?.listen();
                println("Server listening!")
                future.complete()
            } else {
                // Something went wrong during router factory initialization
                var exception: Throwable = openAPI3RouterFactoryAsyncResult.cause();
            }
        });
    }

    override fun stop(){
        this.server?.close()
    }

}
