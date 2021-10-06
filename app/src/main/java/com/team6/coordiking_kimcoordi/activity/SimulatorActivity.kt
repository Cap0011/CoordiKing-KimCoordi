package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.fragment.GalleryFullscreenFragment
import kotlinx.android.synthetic.main.activity_simulator.*

class SimulatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator)
        setUpActionBar()

        btn_Jacket.setOnClickListener {
            //iv_Jacket.setImageResource(R.drawable.image4)
            val intent = Intent(this, MyWardrobeActivity::class.java)
            intent.putExtra("SimulatorActivity", 1)
            intent.putExtra("image", "iv_jacket")
            startActivity(intent)
        }

        btn_Top.setOnClickListener {
            iv_Top.setImageResource(R.drawable.image3)
            Toast.makeText(this@SimulatorActivity, "사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }

        btn_Bottom.setOnClickListener {
            iv_Bottom.setImageResource(R.drawable.image1)
            Toast.makeText(this@SimulatorActivity, "사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_simulator)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_simulator.setNavigationOnClickListener{ onBackPressed()}
    }
}