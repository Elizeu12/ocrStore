package com.belcompany.compras

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.belcompany.compras.data.Element
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CameraActionsFragment : Fragment(R.layout.camera_actions_fragment) {

    private var scan = false
    private var total = 0.0
    private lateinit var model: ViewModelStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_scan).setOnClickListener {
            scan = true
        }

        val textRecognizer = TextRecognizer.Builder(context).build()
        if (!textRecognizer.isOperational) {
            Toast.makeText(
                context,
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

                        var textStringBuilder = stringBuilder.toString().lowercase()

                        var monetary = ""

                        if (textStringBuilder.indexOf("r$") > 0) {
                            monetary = "r$"
                        } else {
                            monetary = "rs"
                        }

                        val textPriceNotTreated = textStringBuilder.substring(
                            textStringBuilder.indexOf(monetary),
                            textStringBuilder.length
                        )
                        var textPrice = ""
                        var count = false
                        var valid = 0

                        textPriceNotTreated.forEach {
                            if (it.isDigit() && valid <= 1) {
                                textPrice += it
                                if (count) {
                                    valid++
                                }
                            }
                            if (it == ',' || it == '.') {
                                textPrice += it
                                count = true
                            }
                        }

                        count = false
                        valid = 0

                        textStringBuilder = textStringBuilder.replaceRange(
                            textStringBuilder.indexOf(monetary),
                            textStringBuilder.length,
                            ""
                        )

                        requireActivity().runOnUiThread {

                            val popupEditText = EditText(context)
                            popupEditText.inputType = InputType.TYPE_CLASS_NUMBER
                            popupEditText.setText("1")
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(resources.getString(R.string.text_confirm_item))
                                .setMessage("item $textStringBuilder \n valor: R$$textPrice")
                                .setView(popupEditText)
                                .setNeutralButton(resources.getString(R.string.txt_btn_cancel)) { _, _ ->
                                }
                                .setPositiveButton(resources.getString(R.string.text_confirm)) { _, _ ->

                                    var valuePrice =
                                        textPrice.replace(',', '.').toDouble()

                                    var value =
                                        popupEditText.text.toString().toDouble() * valuePrice

                                    total += value


                                    model = ViewModelProvider(requireActivity())[ViewModelStore::class.java]

                                    val element = Element(textStringBuilder, value)
                                    model.updateData(element)
                                }
                                .show()
                        }
                        scan = false
                    }
                }
            })

        val cameraContainer = view.findViewById<SurfaceView>(R.id.preview_view)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = CameraSource.Builder(requireContext(), textRecognizer)
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
        }, ContextCompat.getMainExecutor(requireContext()))
        Toast.makeText(context, "Camera abriu xablau", Toast.LENGTH_LONG).show()
    }

}