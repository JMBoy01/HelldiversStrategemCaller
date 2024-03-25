package be.spacepex.helldiversstrategemcaller

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class ClientListenConnectionEstablish(private val port: Int) : Runnable {
    override fun run() {
        var run = true
        while (run) {
            try {
                val serverSocket = ServerSocket(port)
                Log.i("UDP client", "socket created with address ${serverSocket.localSocketAddress} and bound state: ${serverSocket.isBound}")

                val socket = serverSocket.accept()
                Log.i("UDP client", "socket accepting")

                val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                Log.i("UDP client", "about to wait to receive")

                val msg = br.readLine()

                Log.d("UDP, Received data", msg)

                ConnectionUtils.serverIp = msg

                run = false
                br.close()
                socket.close()
                serverSocket.close()

                Log.i("UDP client", "socket closing")
            }catch (e: IOException) {
                Log.e("UDP client has IOException", "error: ", e)
                run = false
            }catch (e: SocketTimeoutException) {
                Log.e("UDP client has SocketTimeoutException", "error: ", e)
                continue
            }
        }

        val socket = Socket(ConnectionUtils.serverIp, port)

        val pw = PrintWriter(socket.getOutputStream())
        pw.write("Server IP arrived successfully!")
        pw.flush()
        pw.close()

        socket.close()
    }
}