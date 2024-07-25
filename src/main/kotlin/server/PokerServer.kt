package server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import java.time.Duration

/**
 * Server
 */
fun startServer() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
            webSocket("/ws") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            outgoing.send(Frame.Text("You said: ${frame.readText()}"))
                        }
                        // Handle other frame types if necessary
                        is Frame.Binary -> TODO()
                        is Frame.Close -> TODO()
                        is Frame.Ping -> TODO()
                        is Frame.Pong -> TODO()
                    }
                }
            }
        }
    }.start(wait = true)
    println("Server started on port 8080")
}
