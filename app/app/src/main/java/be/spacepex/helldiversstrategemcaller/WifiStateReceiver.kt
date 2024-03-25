package be.spacepex.helldiversstrategemcaller

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WifiStateReceiver(private val activity: MainActivity) : BroadcastReceiver() {

    @SuppressLint("SetTextI18n")
    override fun onReceive(context: Context?, intent: Intent?) {
        val ipTextView: TextView = activity.findViewById(R.id.ipTextView)
        ipTextView.text = "Device IP: ${context?.let { ConnectionUtils.getIpAddress(it) }}"
    }

    fun register() {
        val filter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }
        activity.applicationContext.registerReceiver(this, filter)
    }

    fun unregister() {
        activity.applicationContext.unregisterReceiver(this)
    }
}
