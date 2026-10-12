package com.creativedesign.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.creativedesign.webview.databinding.ActivityMainBinding
import com.creativedesign.webview.databinding.LayoutNoInternetBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var offlineBinding: LayoutNoInternetBinding
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private var hasLoadedSuccessfully = false

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val callback = fileUploadCallback ?: return@registerForActivityResult
        fileUploadCallback = null

        val uris = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
        callback.onReceiveValue(uris)
    }

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        offlineBinding = LayoutNoInternetBinding.bind(
            binding.root.findViewById(R.id.offlineLayout)
        )

        if (BuildConfig.ENABLE_FCM && Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setupWebView()
        setupOfflineScreen()
        setupBackNavigation()

        val pushUrl = intent.getStringExtra(EXTRA_PUSH_URL)
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (!pushUrl.isNullOrBlank()) {
                hideOfflineScreen()
                binding.webView.loadUrl(pushUrl)
            } else {
                loadWebsite()
            }
        } else {
            showOfflineScreen()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val appLabel = getString(R.string.app_name).replace(" ", "")
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                userAgentString = "$userAgentString WebViewApp/$appLabel"
            }

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(binding.webView, true)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    hideOfflineScreen()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.swipeRefresh.isRefreshing = false
                    hasLoadedSuccessfully = true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        hasLoadedSuccessfully = false
                        if (!NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                            showOfflineScreen()
                        }
                    }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false

                    return when {
                        url.startsWith("http://") || url.startsWith("https://") -> false
                        url.startsWith("tel:") || url.startsWith("mailto:") -> {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            true
                        }
                        else -> {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                true
                            } catch (_: Exception) {
                                false
                            }
                        }
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    fileUploadCallback?.onReceiveValue(null)
                    fileUploadCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: run {
                        fileUploadCallback = null
                        return false
                    }

                    return try {
                        fileChooserLauncher.launch(intent)
                        true
                    } catch (_: Exception) {
                        fileUploadCallback = null
                        Toast.makeText(
                            this@MainActivity,
                            R.string.file_upload_not_supported,
                            Toast.LENGTH_SHORT
                        ).show()
                        false
                    }
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                binding.webView.reload()
            } else {
                binding.swipeRefresh.isRefreshing = false
                showOfflineScreen()
            }
        }
    }

    private fun setupOfflineScreen() {
        offlineBinding.btnRetry.setOnClickListener {
            offlineBinding.btnRetry.isEnabled = false
            offlineBinding.btnRetry.text = getString(R.string.retrying)

            binding.root.postDelayed({
                if (NetworkUtils.isNetworkAvailable(this)) {
                    offlineBinding.btnRetry.isEnabled = true
                    offlineBinding.btnRetry.text = getString(R.string.retry)
                    hideOfflineScreen()
                    if (hasLoadedSuccessfully) {
                        binding.webView.reload()
                    } else {
                        loadWebsite()
                    }
                } else {
                    offlineBinding.btnRetry.isEnabled = true
                    offlineBinding.btnRetry.text = getString(R.string.retry)
                    Toast.makeText(this, R.string.still_offline, Toast.LENGTH_SHORT).show()
                }
            }, 600)
        }

        offlineBinding.btnOpenSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    binding.webView.canGoBack() -> binding.webView.goBack()
                    else -> showExitDialog()
                }
            }
        })
    }

    private fun showExitDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null)
        val dialog = MaterialAlertDialogBuilder(this, R.style.ExitDialogTheme)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.88f).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialogView.findViewById<MaterialButton>(R.id.btnExit)
            .setOnClickListener {
                dialog.dismiss()
                finish()
            }
        dialogView.findViewById<MaterialButton>(R.id.btnStay)
            .setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun loadWebsite() {
        hideOfflineScreen()
        binding.webView.loadUrl(getString(R.string.website_url))
    }

    private fun showOfflineScreen() {
        offlineBinding.root.visibility = View.VISIBLE
        binding.swipeRefresh.visibility = View.GONE
        offlineBinding.offlineContent.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.offline_enter)
        )
    }

    private fun hideOfflineScreen() {
        offlineBinding.root.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
        CookieManager.getInstance().flush()
    }

    override fun onPause() {
        binding.webView.onPause()
        CookieManager.getInstance().flush()
        super.onPause()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PUSH_URL = "push_url"
    }
}
