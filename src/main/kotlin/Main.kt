import server.startServer
import client.startClient
import view.GameApplication
import kotlinx.coroutines.*

/**
 * Main Function
 */
fun main() {
    // Start the server in a separate thread
//    Thread {
//        startServer()
//    }.start()
//
//    // Wait a few seconds to ensure the server is up and running
//    Thread.sleep(2000)
//
//    // Start the client in a coroutine
//    runBlocking {
//        launch {
//            startClient()
//        }
//    }

    // Instantiate and show the game application.
    GameApplication().show()

    // Print a message indicating that the Shift Poker game has ended.
    println("Shift Poker game ended. Goodbye")
}
