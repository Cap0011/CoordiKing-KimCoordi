package com.team6.coordiking_kimcoordi.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.team6.coordiking_kimcoordi.R
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {

    private val LAST_VIEW_INDEX: Int = 4
    private val FIRST_VIEW_INDEX: Int = 0
    private val DISABLE_TEXT: String = "카메라 사용 가이드라인을 다시 보시려면\n설정-가이드라인에서 다시 가이드라인 보기를 켜 주세요."

    private var guideline_curr_view: Int = 0
    private var is_disable: Boolean = false
    private var disable_alert_flag: Boolean = false

    private var camera_intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
    private var camera_actionistener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("Camera", "take a picture!")
    }
    private var permission_request_code = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        setUpActionBar()

        snap_notify_close.setOnClickListener {
            val read_permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val write_permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val cam_permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

            if (read_permission == PackageManager.PERMISSION_DENIED || write_permission == PackageManager.PERMISSION_DENIED || cam_permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA), permission_request_code)
            }
            else {
                camera_actionistener.launch(camera_intent)
            }
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