package be.spacepex.helldiversstrategemcaller

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var wifiStateReceiver : WifiStateReceiver

    private lateinit var ipText : TextView
    private lateinit var portText : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ipText = findViewById(R.id.ipTextView)
        portText = findViewById(R.id.portTextView)

        portText.text = "Port: ${ConnectionUtils.port}"

        // Registrer the WifiStateReceiver -> updates shown device ip automatically on change
        wifiStateReceiver = WifiStateReceiver(this)
        wifiStateReceiver.register()

        CoroutineScope(Dispatchers.Main).launch {
            ConnectionUtils.establishConnection {
                startNewActivity()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            ConnectionUtils.stopConnectionListener()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiStateReceiver.unregister()
    }

    private fun startNewActivity(){
        val intent = Intent(this, StratMenuButton::class.java)
        startActivity(intent)
        finish()
    }
}