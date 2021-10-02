package com.team6.coordiking_kimcoordi.activity

import android.app.VoiceInteractor
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team6.coordiking_kimcoordi.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private val baseurl = "https://api.openweathermap.org/data/2.5/weather?"
    private val apiKey = "f38c2273c419563935be25a2ad018d7f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateWeather()

        main_btn_wardrobe.setOnClickListener {
            startActivity(Intent(this, MyWardrobeActivity::class.java))
        }
        main_btn_outfit.setOnClickListener {
            startActivity(Intent(this, MyOutfitsActivity::class.java))
        }
        main_btn_camera.setOnClickListener {
            startActivity(Intent(this, SnapActivity::class.java))
        }
        main_btn_mypage.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
        main_btn_favourite.setOnClickListener {
            startActivity(Intent(this, MyWardrobeActivity::class.java))
        }
        main_btn_simulator.setOnClickListener {
            startActivity(Intent(this, SimulatorActivity::class.java))
        }
        main_btn_setting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        main_btn_exit.setOnClickListener {
            val pref = getSharedPreferences("pref", MODE_PRIVATE)
            if(!pref.getBoolean("login",false)){
                Firebase.auth.signOut()
            }
            finish()
        }
    }
    private fun updateWeather(){
        var url = ""
        var city = "London"
        val df = DecimalFormat("##.#")

        url = baseurl + "q=" + city + "&appid=" + apiKey
        var strRequest = StringRequest(Request.Method.POST, url, {
            Log.d("weather", it)
            val obj = JSONObject(it)
            val mainObj = obj.getJSONObject("main")
            val weatherArr = obj.getJSONArray("weather")
            val weatherObj = weatherArr.getJSONObject(0)

            val weather = weatherObj.getString("description")
            val temp = df.format(mainObj.getDouble("temp") - 273.15).toString() + "C"

            main_tv_temp.setText("$temp\n$weather")
            main_tv_city.setText(city)
        }, {
            Log.d("weather", it.toString())
        })
        var requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(strRequest)
    }
}