package com.example.majorproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.dataClass.Product

class TryOn : AppCompatActivity() {
    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_on)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        webView = findViewById(R.id.webView)
        setupWebView()
        WebView.setWebContentsDebuggingEnabled(true)
        // Retrieve the product and send the image URL to JavaScript
        val product = intent.getSerializableExtra("product") as? Product
        val imageUrl = product?.images?.get("0") ?: ""
        Log.d("ImageUrl", "Image URL: $imageUrl")
        if (imageUrl.isNotEmpty()) {
            // Pass the image URL to JavaScript
            webView.evaluateJavascript("loadImage('$imageUrl')", null)
        } else {
            Toast.makeText(this, "No image found for this product", Toast.LENGTH_SHORT).show()
        }
    }




    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {


            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Log.e("WebViewError", "Error: ${error?.description}")
                Toast.makeText(this@TryOn, "Failed to load page", Toast.LENGTH_SHORT).show()
            }


        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                // Log the console message to Logcat
                Log.d("WebView Console", message.message())
                return super.onConsoleMessage(message) // Return the result of the superclass method
            }
        }


        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.let {
                    val resources = it.resources
                    if (resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        if (ContextCompat.checkSelfPermission(
                                this@TryOn,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            it.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                        } else {
                            // Show an alert dialog to explain the permission request
                            showCameraPermissionDialog(it)
                        }
                    } else {
                        it.deny()
                    }
                }
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            mediaPlaybackRequiresUserGesture = false // Allow autoplay
        }

        loadLocalHtml()
    }

    private fun loadLocalHtml() {
        webView.clearCache(true)
        webView.clearHistory()
        webView.loadUrl("file:///android_asset/index.html")
    }

    private fun showCameraPermissionDialog(permissionRequest: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Needed")
            .setMessage("This feature requires camera access. Would you like to grant permission?")
            .setPositiveButton("Allow") { _, _ ->
                pendingPermissionRequest = permissionRequest
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Deny") { dialog, _ ->
                permissionRequest.deny()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingPermissionRequest?.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
            } else {
                Toast.makeText(this, "Camera permission is required for this feature", Toast.LENGTH_LONG).show()
            }
            pendingPermissionRequest = null
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
