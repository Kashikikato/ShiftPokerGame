package client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*


/**
 * client
 */
suspend fun startClient() {
    val client = HttpClient {
        install(WebSockets)
    }

    try {
        client.webSocket(
            method = HttpMethod.Get,
            host = "127.0.0.1",
            port = 8080,
            path = "/ws"
        ) {
            println("Connected to server")

            // Example: Send a message to the server
            send(Frame.Text("Hello, server!"))

            // Example: Read messages from the server
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> println("Received: ${frame.readText()}")
                    // Handle other frame types if necessary
                    is Frame.Binary -> TODO()
                    is Frame.Close -> TODO()
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    } catch (e: Exception) {
        println("Failed to connect to server: ${e.message}")
    }
}
