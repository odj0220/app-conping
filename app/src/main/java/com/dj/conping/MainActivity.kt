package com.dj.conping

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val backKeyHandler = BackKeyHandler(this);
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = initWebview()
    }

    override fun onBackPressed() {
        webView?.loadUrl("javascript:onMessageFromApp('onAndroidBackKeyMessage')")
    }

    private fun initWebview(): WebView {
        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(true)

        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://conping-yqoln5urha-an.a.run.app")

        webView.addJavascriptInterface(WebAppInterface(this, webView, intent), "ConpingInterface")
        webView.setWebViewClient(MyWebViewClient())
        webView.setWebChromeClient(MyWebChromeClient())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        return webView
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            Log.d("LogTag", message)
            result.confirm()
            return super.onJsAlert(view, url, message, result)
        }
    }

    object GlobalStuff {
        var exitStat: Boolean = false;
    }

}