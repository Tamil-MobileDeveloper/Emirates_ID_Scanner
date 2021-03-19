package me.shuza.textrecognization

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.View
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 *
 * :=  created by:  Shuza
 * :=  create date:  28-Jun-18
 * :=  (C) CopyRight Shuza
 * :=  www.shuza.ninja
 * :=  shuza.sa@gmail.com
 * :=  Fun  :  Coffee  :  Code
 *
 **/

class MainActivity : AppCompatActivity() {

    private var mCameraSource by Delegates.notNull<CameraSource>()
    private var textRecognizer by Delegates.notNull<TextRecognizer>()

    private val PERMISSION_REQUEST_CAMERA = 100
    private var idNumber = ""
    private var name = ""
    private var hasValidID = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCameraSource()
    }

    override fun onResume() {
        super.onResume()
//        ivRetry.setOnClickListener {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                requestForPermission()
//            } else {
//                tv_result.visibility = View.GONE
//                ivRetry.visibility = View.GONE
//                hasValidID = false
//                idNumber = ""
//                name = ""
//                mCameraSource.start(surface_camera_preview.holder)
//            }
//        }
    }

    private fun startCameraSource() {

        //  Create text Recognizer
        textRecognizer = TextRecognizer.Builder(this).build()

        if (!textRecognizer.isOperational) {
            toast("Dependencies are not loaded yet...please try after few moment!!")
            Logger.d("Dependencies are downloading....try after few moment")
            return
        }

        //  Init camera source to use high resolution and auto focus
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(surface_camera_preview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    toast("Error:  ${e.message}")
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                } else {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i).value
                        stringBuilder.append(item)
                        stringBuilder.append("\n")

                        if (Pattern.matches(
                                        "([0-9]{3})*-([0-9]{4})*-([0-9]{7})*-([0-9])",
                                        item
                                )
                        ) {
                            hasValidID = true
                            idNumber = item
                            println("idNumber $idNumber")
                        }
                        if (item.contains("Name", true))
                            name = item.replace("Name:", "");
                    }
                    if (hasValidID) {
                        runOnUiThread {
                            mCameraSource.stop()
//                            tv_result.visibility = View.VISIBLE
//                            ivRetry.visibility = View.VISIBLE
//                            ivDone.visibility = View.VISIBLE
//                            tv_result.text = "ID: $idNumber \n $name"
                            progress.visibility = View.VISIBLE
                            Handler().postDelayed(Runnable {
                                progress.visibility = View.GONE
                                startActivity(
                                        Intent(
                                                this@MainActivity,
                                                PassengerInformationActivity::class.java
                                        ).apply {
                                            putExtras(Bundle().apply {
                                                putString(ARG_ID_NUMBER, idNumber)
                                                putString(ARG_NAME, name)
                                            })
                                        }
                                )
                            }, 2000)


                        }
                    }
                }
            }
        })
    }

    private fun checkValidNumberRead(readString: String?): Boolean {
        return when {
            readString.isNullOrEmpty() -> {
                println("Hiii Error 1: String shouldn't be empty $readString")
                false
            }
            readString!!.length < 18 -> {
                println("Hiii Error 2: Id should be greater than 18 $readString")
                false
            }
            !Pattern.matches("([0-9]{3})*-([0-9]{4})*-([0-7]{7})*-[0-9]", readString) -> {
                println("Hiii Error 3: Id format invalid $readString")
                false
            }
            else -> {
                println("Hiii Success $readString")
                true
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                mCameraSource.start(surface_camera_preview.holder)
            } else {
                toast("Permission need to grant")
                finish()
            }
        }
    }
}
