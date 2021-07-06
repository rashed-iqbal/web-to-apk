package com.rashediqbal.webtoapk

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webToApk: WebToApk

    private val WEB_URL: String = "https://rust-lang.org"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView:WebView = findViewById(R.id.webView)
        webView.loadUrl(WEB_URL)
        webToApk = WebToApk(webView)

    }

    override fun onBackPressed() {
        webToApk.exitDialog()
    }
}