package com.belcompany.compras

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belcompany.compras.data.Element
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var arrayList: ArrayList<Element>
    private var scan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycle_view)

        arrayList = arrayListOf()

        recycler.adapter = Adapter(arrayList)


        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        findViewById<Button>(R.id.btn_scan).setOnClickListener {
            scan = true
        }

        if (allPermissionsGranted()) {
                startCamera()
        } else {
            getPermission.launch(RequiresPermission)
        }

    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        RequiresPermission
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val RequiresPermission = android.Manifest.permission.CAMERA
    }

    private fun startCamera() {

        val textRecognizer = TextRecognizer.Builder(this).build()
        if (!textRecognizer.isOperational) {
            Toast.makeText(
                this,
                "Dependencies are not loaded yet...please try after few moment!!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        textRecognizer.setProcessor(
            object : Detector.Processor<TextBlock> {
                override fun release() {}

                @SuppressLint("HandlerLeak")
                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items = detections.detectedItems

                    if (items.size() <= 0) {
                        return
                    }

//                    findViewById<TextView>(R.id).post {
//                        val stringBuilder = StringBuilder()
//                        for (i in 0 until items.size()) {
//                            val item = items.valueAt(i)
//                            stringBuilder.append(item.value)
//                            stringBuilder.append("\n")
//                        }
                    if (scan) {
                        val stringBuilder = StringBuilder()
                        for (i in 0 until items.size()) {
                            val item = items.valueAt(i)
                            stringBuilder.append(item.value)
                        }

                        val recycler = findViewById<RecyclerView>(R.id.recycle_view)

                        var textStringBuilder = stringBuilder.toString()

                        val textPriceNotTreated = textStringBuilder.substring(
                            textStringBuilder.indexOf("R$"),
                            textStringBuilder.length
                        )
                        var textPrice = ""
                        var count = false
                        var valid = 0

                        textPriceNotTreated.forEach {
                            if (it.isDigit() && valid <= 1){
                                textPrice += it
                                if (count){
                                    valid ++
                                }
                            }
                            if (it == ','){
                                textPrice += it
                                count = true
                            }
                        }

                        count = false
                        valid = 0

                        textStringBuilder = textStringBuilder.replaceRange(
                            textStringBuilder.indexOf("R$"),
                            textStringBuilder.length,
                            ""
                        )

                        runOnUiThread {
                            MaterialAlertDialogBuilder(this@MainActivity)
                                .setTitle(resources.getString(R.string.text_confirm_item))
                                .setMessage("item $textStringBuilder \n valor: R$$textPrice")
                                .setNeutralButton(resources.getString(R.string.txt_btn_cancel)) { dialog, which ->

                                }
                                .setPositiveButton(resources.getString(R.string.text_confirm)) { dialog, which ->
                                    var texto = Element(textStringBuilder, "R$$textPrice")

                                    arrayList.add(texto)

                                    recycler.adapter?.notifyDataSetChanged()
                                }
                                .show()
                        }
                        scan = false
                    }
                }
            })

        val cameraContainer = findViewById<SurfaceView>(R.id.preview_view)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

            cameraContainer.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                }

                override fun surfaceDestroyed(p0: SurfaceHolder) {
                    preview.stop()
                }

                @SuppressLint("MissingPermission")
                override fun surfaceCreated(p0: SurfaceHolder) {
                    try {
                        cameraProvider.unbindAll()
                        preview.start(cameraContainer.holder)
                    } catch (e: Exception) {
                        Log.e("logCamera", "Failure initialize camera", e)
                    }
                }
            })
        }, ContextCompat.getMainExecutor(this))
        Toast.makeText(this, "Camera abriu xablau", Toast.LENGTH_LONG).show()
    }

    private val getPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "sem permissão para acessar a camera", Toast.LENGTH_LONG)
                    .show()
            }
        }
}
