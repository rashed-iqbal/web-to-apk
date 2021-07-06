package com.rashediqbal.webtoapk

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class WebToApk(private val webView: WebView) {

     init{
         true.also { webView.settings.javaScriptEnabled = it }
        webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = webView.settings
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
    }

    fun exitDialog(){
        if(webView.canGoBack()){
            webView.goBack()
        } else {
            val dialogBuilder = AlertDialog.Builder(webView.context)
            dialogBuilder
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes"){ _, _ ->
                    (webView.context as Activity).finish()
                }
                .setNegativeButton("No"){ dialogInterface, _ ->
                    dialogInterface.cancel()
                }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }
    }



    fun checkPermission(permission: String,requestCode:Int){
        if(ContextCompat.checkSelfPermission(webView.context , permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(webView.context as Activity, arrayOf(permission),requestCode)
        }
    }

    fun enableDownload(webView:WebView){
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->

            val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimetype)
            request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
            request.addRequestHeader("User-Agent",userAgent)
            request.setDescription("Download file...")
            request.allowScanningByMediaScanner()
            request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimetype))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype))
            val downloadManager:DownloadManager = webView.context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(webView.context,"Downloading File",Toast.LENGTH_SHORT).show()

        }
    }



    fun progressBar(progressBar:ProgressBar,startProgress:ProgressBar? = null){
        // Progress Bar
        var isStart = true

        if (startProgress == null){

            webView.webChromeClient = object: WebChromeClient(){
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.progress = newProgress
                    if(newProgress <100 && progressBar.visibility == ProgressBar.GONE){
                        progressBar.visibility = ProgressBar.VISIBLE
                    }
                    if(newProgress == 100){
                        progressBar.visibility = ProgressBar.GONE
                    }

                }

            }

        } else {
            webView.webChromeClient = object: WebChromeClient(){
                override fun onProgressChanged(view: WebView?, newProgress: Int) {

                    progressBar.progress = newProgress
                    startProgress!!.progress = newProgress

                    if(isStart){
                        if(newProgress < 100 && startProgress.visibility == ProgressBar.GONE){
                            startProgress.visibility = ProgressBar.VISIBLE
                        }
                        if(newProgress == 100 && startProgress.visibility == ProgressBar.VISIBLE){
                            startProgress.visibility = ProgressBar.GONE
                            isStart = false
                        }

                    } else {
                        if(newProgress <100 && progressBar.visibility == ProgressBar.GONE){
                            progressBar.visibility = ProgressBar.VISIBLE
                        }
                        if(newProgress == 100){
                            progressBar.visibility = ProgressBar.GONE
                        }
                    }

                }

            }
        }


    }

    fun pullToRefresh(swipeRefresh:SwipeRefreshLayout){
        swipeRefresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefresh.isRefreshing = false
                webView.reload()
            },500)
        }
    }


    fun checkInternet(isInternet:(value:Boolean)->Unit){

        val connectivityManager = webView.context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request,object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isInternet(true)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                isInternet(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isInternet(false)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                isInternet(false)
            }

        })


    }



}