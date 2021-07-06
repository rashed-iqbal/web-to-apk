package com.rashediqbal.webtoapk

import android.os.Bundle
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webToApk: WebToApk

    private val WEB_URL: String = "https://rust-lang.org"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView:WebView = findViewById(R.id.web_view)
        webView.loadUrl(WEB_URL)
        webToApk = WebToApk(webView)

        val progressBar:ProgressBar = findViewById(R.id.progress_bar)
        val startProgress:ProgressBar = findViewById(R.id.start_progress)

        webToApk.progressBar(progressBar)

    }

    override fun onBackPressed() {
        webToApk.exitDialog()
    }
}