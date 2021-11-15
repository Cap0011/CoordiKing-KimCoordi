package com.team6.coordiking_kimcoordi.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ExifInterface
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
import com.team6.coordiking_kimcoordi.adapter.GalleryImageAdapter
import com.team6.coordiking_kimcoordi.adapter.GalleryImageClickListener
import com.team6.coordiking_kimcoordi.adapter.Image
import com.team6.coordiking_kimcoordi.databinding.ActivitySnapBinding
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {

    private val LAST_VIEW_INDEX: Int = 4
    private val FIRST_VIEW_INDEX: Int = 0
    private val DISABLE_TEXT: String = "카메라 사용 가이드라인을 다시 보시려면\n설정-가이드라인에서 다시 가이드라인 보기를 켜 주세요."
    private val NOTIFY_TEXT: String = "우리는 사진을 찍을 때 직관적이고 명확하게 애플리케이션을 사용할 수 있게 사진을 찍는 가이드라인을 제공하고 있습니다. 가이드라인을 한 번 읽고 사용해주세요."
    private val GUIDELINE_TEXT = arrayOf(
        "너무 어둡거나 너무 밝은 곳은 피해 주세요!",
        "옷의 전체 형태가 화면 안에 들어오게 찍어 주세요!",
        "옷이 구겨지지 않게 잘 편 후 찍어 주세요!",
        "정방형으로 찍기를 권장 드려요!"
    )

    private var guideline_curr_view: Int = 0
    private var permission_request_code: Int = 1
    // no use -> 대체: MyApplication.is_guideline_disable
    //private var is_disable: Boolean = false
    private var disable_alert_flag: Boolean = false
    //guideline view test
    private var image_list: Array<Int> = arrayOf(R.drawable.guide0, R.drawable.guide1, R.drawable.guide2, R.drawable.guide3)

    private var camera_intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
    private var camera_actionistener = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_CANCELED) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("메뉴 선택")
            builder.setMessage("선택할 사진이 추가될 메뉴를 고르세요.")
            builder.setPositiveButton(
                "MyOutfits", {
                        dialogInterface: DialogInterface?, i -> Int
                    var intent = Intent(this, MyOutfitsActivity::class.java)
                    intent.putExtra("snap", "from snap activity")
                    startActivity(intent)
                }
            )
            builder.setNegativeButton(
                "MyWardRobe", {
                        dialogInterface: DialogInterface?, i -> Int
                    var intent = Intent(this, MyWardrobeActivity::class.java)
                    intent.putExtra("snap", "from snap activity")
                    startActivity(intent)
                }
            )
            builder.setNeutralButton(
                "cancel", {
                        dialogInterface: DialogInterface?, i -> Int
                    //cancel
                }
            )
            builder.setCancelable(false)
            builder.show()

        }

//        var add_intent = Intent(this, ImageAddActivity::class.java)
//
//        startActivityForResult(add_intent, 10)
        //finish()
    }

    lateinit var binding: ActivitySnapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySnapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        binding.snapNotifyClose.setOnClickListener {
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
        binding.snapNotifyPrev.setOnClickListener {
            if (guideline_curr_view < FIRST_VIEW_INDEX + 1) {
                Toast.makeText(applicationContext, "첫 번째 가이드라인입니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                guideline_curr_view--
                // 이전 가이드라인 텍스트로 설정하기
                binding.snapNotifyView.text = GUIDELINE_TEXT[guideline_curr_view]
                // 이전 가이드라인 사진으로 넘기기
                binding.snapNotifyImageView.setImageResource(image_list[guideline_curr_view])
            }
        }

        binding.snapNotifyNext.setOnClickListener {
            if (guideline_curr_view >= LAST_VIEW_INDEX - 1) {
                Toast.makeText(applicationContext, "마지막 가이드라인입니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                guideline_curr_view++
                // 다음 가이드라인 텍스트로 설정하기
                binding.snapNotifyView.text = GUIDELINE_TEXT[guideline_curr_view]
                // 다음 가이드라인 사진으로 넘기기
                binding.snapNotifyImageView.setImageResource(image_list[guideline_curr_view])
            }
        }

        binding.snapNotifyDisable.setOnCheckedChangeListener { buttonView, isChecked ->
            //if (!is_disable) Toast.makeText(applicationContext, "다시 보기는 설정에서", Toast.LENGTH_SHORT).show()
            if (!disable_alert_flag) {
                var disable_alert = AlertDialog.Builder(this)
                disable_alert.setMessage(DISABLE_TEXT)
                disable_alert.setPositiveButton("확인", null)
                disable_alert.show()
                disable_alert_flag = true
            }
            MyApplication.is_guideline_disable = !MyApplication.is_guideline_disable
        }

        var notify_alert = AlertDialog.Builder(this)
        notify_alert.setMessage(NOTIFY_TEXT)
        notify_alert.setPositiveButton("확인", null)
        notify_alert.show()

        binding.snapNotifyView.text = GUIDELINE_TEXT[FIRST_VIEW_INDEX]
        binding.snapNotifyImageView.setImageResource(image_list[guideline_curr_view])

        if (MyApplication.is_guideline_disable) {
            camera_actionistener.launch(camera_intent)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(tb_snap)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        tb_snap.setNavigationOnClickListener{ onBackPressed() }
    }

}