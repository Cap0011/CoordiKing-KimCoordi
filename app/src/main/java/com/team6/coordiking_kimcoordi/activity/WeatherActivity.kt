package com.team6.coordiking_kimcoordi.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_outfits.*
import kotlinx.android.synthetic.main.activity_weather.*
import org.json.JSONObject
import java.text.DecimalFormat

class WeatherActivity : AppCompatActivity() {
    private val baseurl = "https://api.openweathermap.org/data/2.5/weather?"
    private val apiKey = "f38c2273c419563935be25a2ad018d7f"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_weather)
        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_weather)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        getWeatherHere()

        tb_weather.setNavigationOnClickListener{ startActivity(Intent(this, MainActivity::class.java))}
    }

    private fun updateWeather(lat: Double, lon: Double){
        var url = ""
        lateinit var city: String
        val df = DecimalFormat("##.#")

        url = baseurl + "lat=" + lat.toString() + "&lon=" + lon.toString() + "&appid=" + apiKey
        var strRequest = StringRequest(Request.Method.POST, url, {
            Log.d("weather", it)
            val obj = JSONObject(it)
            val mainObj = obj.getJSONObject("main")
            val weatherArr = obj.getJSONArray("weather")
            val weatherObj = weatherArr.getJSONObject(0)

            val weather = weatherObj.getString("description")
            val temp = df.format(mainObj.getDouble("temp") - 273.15).toString() + "C"
            city = obj.getString("name")

            tv_city.text = "City name: $city"
            tv_description.text = "Weather: $weather"
            tv_temp.text = "Current temperature: $temp"
        }, {
            Log.d("weather", it.toString())
        })
        var requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(strRequest)
    }
    private fun getWeatherHere(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                // Got last known location. In some rare situations this can be null.
                if(it == null) return@addOnSuccessListener

                Log.d("weather", it.toString())
                var lat = it.latitude
                var lon = it.longitude
                updateWeather(lat, lon)
            }
    }
}