package com.team6.coordiking_kimcoordi.activity

import android.Manifest
import android.app.VoiceInteractor
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.team6.coordiking_kimcoordi.*
import com.team6.coordiking_kimcoordi.adapter.Clothes
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.adapter.WardrobeTag
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    // 추가-------------------------
    private var imageList = ArrayList<Image>()
    private var imageListBackUp = ArrayList<Image>()
    lateinit var galleryAdapter: GalleryImageAdapter
    val database = Firebase.database.reference
    val storage = Firebase.storage
    lateinit var user: FirebaseUser
    var myWardrobe: MutableList<Clothes> = arrayListOf()
    var myWardrobeTagList: MutableList<WardrobeTag> = arrayListOf()
    var myWardrobeTagListBackUp: MutableList<WardrobeTag> = arrayListOf()
    var typeArr = arrayOf("jacket", "top", "bottom")
    // ------------------------------

    private val baseurl = "https://api.openweathermap.org/data/2.5/weather?"
    private val apiKey = "f38c2273c419563935be25a2ad018d7f"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateWeather("Manchester")

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
            startActivity(Intent(this, FavouriteActivity::class.java))
        }
        main_btn_simulator.setOnClickListener {
            startActivity(Intent(this, SimulatorActivity::class.java))
        }

        main_btn_setting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        
        main_btn_random.setOnClickListener {
            showRandom()           
        }

        main_btn_exit.setOnClickListener {
            val pref = getSharedPreferences("pref", MODE_PRIVATE)
            if(!pref.getBoolean("login",false)){
                Firebase.auth.signOut()
            }
            finish()
        }
        main_btn_search.setOnClickListener {
            val city = main_et_city.text.toString()
            if(city != ""){
                updateWeather(city)
            } else{
                Toast.makeText(baseContext, "Please enter the city name", Toast.LENGTH_SHORT).show()
            }
        }
        main_btn_loc.setOnClickListener {
            getWeatherHere()
        }
    }

    private fun showRandom() {
        // 랜덤 코디 제공
        iv_main.setImageResource(R.drawable.guide0)

    }

    private fun updateWeather(name: String){
        var url = ""
        var city = name
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
            main_et_city.setHint(city)
            main_et_city.setText("")
        }, {
            Log.d("weather", it.toString())
        })
        var requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(strRequest)
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

            main_tv_temp.setText("$temp\n$weather")
            main_et_city.setHint(city)
        }, {
            Log.d("weather", it.toString())
        })
        var requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(strRequest)
    }
    private fun getWeatherHere(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
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
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 99)
    }

}