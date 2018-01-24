package ftl

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object Main {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val server = embeddedServer(Netty, 4010) {
            routing {
                // POST /v1/projects/{projectId}/testMatrices
                get("/") {
                    call.respondText("Hello, world!", ContentType.Text.Html)
                }
            }
        }
        server.start(wait = true)
    }
}
