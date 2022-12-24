package com.belcompany.compras

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.Logger
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycle_view)


        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

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

                    val stringBuilder = StringBuilder()
                        for (i in 0 until items.size()) {
                            val item = items.valueAt(i)
                            stringBuilder.append(item.value)
                            stringBuilder.append("\n")
                    }
                    data.element.forEach {
                        arrayList.add(it)
                    }

                    recycler.adapter = Adapter(arrayList)

                    val texto = stringBuilder.toString()
                }
            })

        val cameraContainer = findViewById<SurfaceView>(R.id.previewView)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
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