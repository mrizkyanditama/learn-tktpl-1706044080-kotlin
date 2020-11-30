package id.ac.ui.cs.mobileprogramming

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("/")
    fun postWifiModels(@Body wifiModelList: List<WifiModel>): Call<ResponseBody>
}