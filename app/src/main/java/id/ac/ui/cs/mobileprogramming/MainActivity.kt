package id.ac.ui.cs.mobileprogramming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var wifiAdapter: WifiAdapter
    lateinit var wifiManager: WifiManager
    var wifiList = mutableListOf<WifiModel>()
    private var wifiBroadcastReceiver = WifiBroadcastReceiver()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        this.requestPermissions(arrayOf("android.permission.ACCESS_COARSE_LOCATION"), 1)
        this.requestPermissions(arrayOf("android.permission.ACCESS_FINE_LOCATION"), 1)

        wifiAdapter = WifiAdapter(wifiList, applicationContext)
        listview_wifi.adapter = wifiAdapter

        switch_wifi.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (v is Switch) {
                    wifiManager.isWifiEnabled = v.isChecked
                }
            }
        })

        button_send.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val call = ApiClient().getService().postWifiModels(wifiList)
                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {
                        Log.v("SUCCESS", response.body().toString() + " ")
                    }

                    override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                        Toast.makeText(this@MainActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })

        wifiRegisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiBroadcastReceiver)
    }

    fun wifiRegisterReceiver() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        registerReceiver(wifiBroadcastReceiver, intentFilter)
    }

    private fun isExistedConfiguration(scanResult: ScanResult): Boolean {
        val configurationList = wifiManager.configuredNetworks
        for (wifiConfiguration in configurationList) {
            if (wifiConfiguration.SSID.equals("\"" + scanResult.SSID + "\"")) {
                return true
            }
        }
        return false
    }

    external fun convertToGHzJNI(input: Double): Double

    private fun packageWifiItem(scanResult: ScanResult, info: String = ""): WifiModel {
        return WifiModel(
                frequency = convertToGHzJNI(scanResult.frequency.toDouble()).toString(),
                info = info,
                ssid = scanResult.SSID,
                bssid = scanResult.BSSID,
                rssi = WifiManager.calculateSignalLevel(scanResult.level, 10).toString())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun updateDataAndView() {
        var isFlag = false
        println("wifiManager.scanResults.size: " + wifiManager.scanResults.size)
        wifiList.clear()
        for (scanResult in wifiManager.scanResults) {
            if (isExistedConfiguration(scanResult)) {

                if ((wifiManager.connectionInfo.ssid == "\"" + scanResult.SSID + "\"")
                        and (wifiManager.connectionInfo.bssid == scanResult.BSSID)) {
                    wifiList.add(0, packageWifiItem(scanResult, "Terhubung"))
                    isFlag = true

                } else {
                    wifiList.add(if (isFlag) 1 else 0, packageWifiItem(scanResult, "Tersimpan"))

                }
            } else{
                wifiList.add(packageWifiItem(scanResult))
            }
            println(scanResult)
        }
        wifiAdapter.notifyDataSetChanged()
    }


    inner class WifiBroadcastReceiver: BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context?, intent: Intent?) {

            println("ACTION: " + intent?.action)
            when(intent?.action) {
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    var state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
                    when (state) {
                        1 -> {
                            switch_wifi.isChecked = false
                            updateDataAndView()
                        }

                        3 -> {
                            switch_wifi.isChecked = true
                            wifiManager.startScan()
                            updateDataAndView()
                        }
                    }

                }
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    updateDataAndView()
                }
                WifiManager.RSSI_CHANGED_ACTION -> {
                    updateDataAndView()
                }
            }
        }
    }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}