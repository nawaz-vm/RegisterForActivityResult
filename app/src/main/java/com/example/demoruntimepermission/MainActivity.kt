package com.example.demoruntimepermission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import com.example.demoruntimepermission.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 0
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var registerActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            if (hasCamera(applicationContext)) {
                checkForPermissionAndLaunchCamera()
            } else {
                Toast.makeText(applicationContext, "No Camera fund", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    val intent = Intent()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Without camera is not granted. without the camera permission not able to launch the camera",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        registerActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = it.data?.extras?.get("data") as Bitmap
                    binding.imageView.setImageBitmap(imageBitmap)
                }
            }

    }


    private fun checkForPermissionAndLaunchCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    launchCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Alert")
                    dialog.setMessage("Camera permission is required in order to capture image")
                    dialog.setPositiveButton("Allow"
                    ) { d, _ ->
                        d.dismiss()
                        requestPermission(Manifest.permission.CAMERA)
                    }
                    dialog.setNegativeButton("Deny"
                    ) { d, _ ->
                        d.dismiss()
                    }
                    dialog.setCancelable(true)
                    dialog.show()

                }
                else -> {
                    // You can directly ask for the permission.
                    requestPermission(Manifest.permission.CAMERA)
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    launchCamera()
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                            permissions,
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "permission granted", Toast.LENGTH_SHORT)
                        .show()
                    checkForPermissionAndLaunchCamera()
                } else {
                    Toast.makeText(applicationContext, "permission not granted", Toast.LENGTH_SHORT)
                        .show()

                }
                return
            }
            else -> {
                //Ignore/ check for other type of request code
            }

        }

    }

    private fun requestPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }


    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        registerActivityResultLauncher.launch(takePictureIntent)
    }


    private fun hasCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

}
