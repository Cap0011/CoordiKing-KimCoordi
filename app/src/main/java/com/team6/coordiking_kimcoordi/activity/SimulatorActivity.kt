package com.team6.coordiking_kimcoordi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.team6.coordiking_kimcoordi.R
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.databinding.ActivitySimulatorBinding
import com.team6.coordiking_kimcoordi.fragment.MyWardrobeFragment
import kotlinx.android.synthetic.main.activity_simulator.*

class SimulatorActivity : AppCompatActivity(), MyWardrobeFragment.OnFragmentInteractionListener {

    lateinit var binding: ActivitySimulatorBinding
    private var imageList = ArrayList<Image>()
    private var selectedPosition: Int = 0
    private var clickButton = -1
    private val ISJACKET = 1
    private val ISTOP = 2
    private val ISBOTTOM = 3

    // MyWardrobeFragment에서 이미지 데이터를 받기
    override fun onFragmentInteraction(bundle: Bundle) {
        imageList = bundle?.getSerializable("images") as ArrayList<Image>
        selectedPosition = bundle?.getInt("position")
        val image = imageList.get(selectedPosition)

        //storage 이미지 다운로드
        val imgRef= MyApplication.storage
            .reference
            .child("${MyApplication.user!!.uid}/${image.title}.png").downloadUrl.addOnSuccessListener {
                // load image
                when (clickButton){
                    ISJACKET -> Glide.with(this).load(it).into(iv_Jacket)
                    ISTOP -> Glide.with(this).load(it).into(iv_Top)
                    ISBOTTOM -> Glide.with(this).load(it).into(iv_Bottom)
                }
            }.addOnCanceledListener {
                Log.d("SimulatorActivity","failed to download")
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySimulatorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setContentView(R.layout.activity_simulator)
        setUpActionBar()

        btn_Jacket.setOnClickListener {
            clickButton = ISJACKET
            var transaciton = supportFragmentManager.beginTransaction()
                .add(R.id.framelayout, MyWardrobeFragment())
                .commit()
        }

        btn_Top.setOnClickListener {
            clickButton = ISTOP
            var transaciton = supportFragmentManager.beginTransaction()
                .add(R.id.framelayout, MyWardrobeFragment())
                .commit()
        }

        btn_Bottom.setOnClickListener {
            clickButton = ISBOTTOM
            var transaciton = supportFragmentManager.beginTransaction()
                .add(R.id.framelayout, MyWardrobeFragment())
                .commit()
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