package be.spacepex.helldiversstrategemcaller

import android.content.Intent
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException


class ClientListenServerStop(private val port: Int) : Runnable {
    private lateinit var serverSocket: ServerSocket
    private lateinit var socket: Socket
    private lateinit var br: BufferedReader


    override fun run() {
        var run = true
        while (run) {
            try {
                serverSocket = ServerSocket(port)
                socket = serverSocket.accept()
                br = BufferedReader(InputStreamReader(socket.getInputStream()))

                val msg = br.readLine()

                if(msg == "STOP"){
                    startNewActivity()
                }

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
            finally {
                if (::br.isInitialized) {
                    br.close()
                }
                if (::socket.isInitialized) {
                    socket.close()
                }
                if (::serverSocket.isInitialized) {
                    serverSocket.close()
                }
            }
        }
    }

    private fun startNewActivity(){
        val currentActivity = ConnectionUtils.currentActivity

        val intent = Intent(currentActivity.applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        currentActivity.applicationContext.startActivity(intent)

        currentActivity.finish()
    }
}