package id.ac.ui.cs.mobileprogramming

import com.google.gson.annotations.SerializedName

data class WifiModel (
        @SerializedName("info")
        val info: String,
        @SerializedName("frequency")
        val frequency: String,
        @SerializedName("ssid")
        val ssid: String,
        @SerializedName("bssid")
        val bssid: String,
        @SerializedName("rssi")
        val rssi: String
){
        fun getSsidText(): String = "SSID: $ssid"
        fun getFrequencyText(): String = "Freq: $frequency"
        fun getBssidText(): String = "BSSID: $bssid"
        fun getRssiText(): String = "RSSI: $rssi"
        fun getInfoText(): String = "Info: $info"
}