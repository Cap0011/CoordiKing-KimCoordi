package com.team6.coordiking_kimcoordi.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {

    private val LAST_VIEW_INDEX: Int = 4
    private val FIRST_VIEW_INDEX: Int = 0
    private val DISABLE_TEXT: String = "카메라 사용 가이드라인을 다시 보시려면\n설정-가이드라인에서 다시 가이드라인 보기를 켜 주세요."

    private var guideline_curr_view: Int = 0
    private var is_disable: Boolean = false
    private var disable_alert_flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        setUpActionBar()

        snap_notify_view.setText("이 페이지는 사진을 찍을 때의 가이드라인을 제시합니다.")

        snap_notify_close.setOnClickListener {
            // 카메라 권한 요청 및 카메라 사진 촬영 기능
        }

        snap_notify_prev.setOnClickListener {
            if (guideline_curr_view == FIRST_VIEW_INDEX) {
                Toast.makeText(applicationContext, "no more prev guideline.", Toast.LENGTH_SHORT).show()
            }
            else {
                guideline_curr_view--
                // 이전 가이드라인 사진으로 넘기기기
                // 이전 가이드라인 텍스트로 설정하기
            }
        }

        snap_notify_next.setOnClickListener {
            if (guideline_curr_view == LAST_VIEW_INDEX) {
                Toast.makeText(applicationContext, "no more next guideline", Toast.LENGTH_SHORT).show()
            }
            else {
                guideline_curr_view++
                // 다음 가이드라인 사진으로 넘기기
                // 다음 가이드라인 텍스트로 설정하기
            }
        }

        snap_notify_disable.setOnCheckedChangeListener { buttonView, isChecked ->
            //if (!is_disable) Toast.makeText(applicationContext, "다시 보기는 설정에서", Toast.LENGTH_SHORT).show()
            if (!disable_alert_flag) {
                var disable_alert = AlertDialog.Builder(this)
                disable_alert.setMessage(DISABLE_TEXT)
                disable_alert.setPositiveButton("OK", null)
                disable_alert.show()
                disable_alert_flag = true
            }
            is_disable = !is_disable

        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(tb_snap)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_snap.setNavigationOnClickListener{ onBackPressed()}
    }
}