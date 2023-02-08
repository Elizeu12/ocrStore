package com.belcompany.compras

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container_cam, CameraActionsFragment())
                .add(R.id.container_list, RecyclerListFragment())
                .commit()
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

    private val getPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            if (allPermissionsGranted()) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_cam, CameraActionsFragment())
                    .add(R.id.container_list, RecyclerListFragment())
                    .commit()
            } else {
                Toast.makeText(this, "sem permissão para acessar a camera", Toast.LENGTH_LONG)
                    .show()
            }
        }
}
