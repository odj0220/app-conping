package com.dj.conping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)

        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://192.168.0.21:3000/apptest")

        webView.addJavascriptInterface(WebAppInterface(this, webView), "ConpingInterface")
        webView.setWebViewClient(MyWebViewClient())
        webView.setWebChromeClient(MyWebChromeClient())
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            view.loadUrl("javascript:alert(showVersion('called by Android'))")
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            Log.d("LogTag", message)
            result.confirm()
            return super.onJsAlert(view, url, message, result)
        }
    }
}