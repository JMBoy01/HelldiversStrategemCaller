package be.spacepex.helldiversstrategemcaller
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.Socket

@SuppressLint("StaticFieldLeak")
object ConnectionUtils {
    var serverIp: String = "SERVER_IP"
    val port: Int = 7070
    var establishConnectionJob: Job? = null

    lateinit var currentActivity: Activity

    fun getIpAddress(context: Context): String {
        // WifiManager initialiseren om het IP-adres op te halen
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Controleren of wifi is ingeschakeld
        if (wifiManager.isWifiEnabled) {
            // WifiInfo object ophalen dat informatie bevat over de huidige wifi-verbinding
            val wifiInfo: WifiInfo = wifiManager.connectionInfo

            // Het IP-adres in integer notatie verkrijgen
            val ipAddress: Int = wifiInfo.ipAddress

            // Het integer IP-adres converteren naar een leesbaar formaat
            return String.format(
                "%d.%d.%d.%d",
                ipAddress and 0xff,
                ipAddress shr 8 and 0xff,
                ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff
            )
        } else {
            return "Enable wifi"
        }
    }

    suspend fun establishConnection(onCompletion: () -> Unit){
        val job = coroutineContext[Job]
        establishConnectionJob = job

        val udpConnect = ClientListenConnectionEstablish(port)

        withContext(Dispatchers.IO){
            udpConnect.run()
        }
        onCompletion()
    }

    suspend fun sendButtonPress(data: String){
        withContext(Dispatchers.IO){
            val socket = Socket(serverIp, port)

            val pw = PrintWriter(socket.getOutputStream())
            pw.write(data)
            pw.flush()
            pw.close()

            socket.close()
        }
    }

    suspend fun stopConnectionListener(){
        withContext(Dispatchers.IO){
            val udpStop = ClientListenServerStop(port-1)
            udpStop.run()
        }
    }
}