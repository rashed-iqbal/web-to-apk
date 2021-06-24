package com.rashediqbal.webtoapk

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class WebToApk (private val context: Context){

    fun exitDialog(){
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes"){ _, _ ->
                (context as Activity).finish()
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    fun checkPermission(permission: String,requestCode:Int){
        if(ContextCompat.checkSelfPermission(context , permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(permission),requestCode)
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
            val downloadManager:DownloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context,"Downloading File",Toast.LENGTH_SHORT).show()

        }
    }



}