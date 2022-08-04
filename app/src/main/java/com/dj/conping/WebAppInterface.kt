package com.dj.conping

import android.app.AlertDialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import org.json.JSONObject


var CALLBACK_FUNCTION = "conpingCallback"

class WebAppInterface// Instantiate the interface and set the context
internal constructor(internal var mContext: Context, var webView: WebView) {

    fun callBack(eventId: String, data: Any = "") {
        webView.post(Runnable {
            webView.loadUrl("javascript:" + CALLBACK_FUNCTION + "('" + eventId + "','" + data + "')")
        })
    }

    val androidVersion: Int
    @JavascriptInterface
    get() = android.os.Build.VERSION.SDK_INT

    @JavascriptInterface
    fun callToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun callAlert(map: String) {
        val map: JSONObject = JSONObject(map)
        val alert = AlertDialog.Builder(mContext)
        if (map.has("title")) {
            alert.setTitle(map.getString("title"))
        }
        if (map.has("message")) {
            alert.setMessage(map.getString("message"))
        }
        alert.create()
        alert.show()
    }

    @JavascriptInterface
    fun callConfirm(map: String, eventId: String) {
        val map: JSONObject = JSONObject(map)
        val alert = AlertDialog.Builder(mContext)
        if (map.has("title")) {
            alert.setTitle(map.getString("title"))
        }
        if (map.has("message")) {
            alert.setMessage(map.getString("message"))
        }
        alert.setNegativeButton("취소", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.d("Confirm", "negative")
                callBack(eventId);
            }
        } ).setCancelable(false)
        alert.setPositiveButton("확인", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.d("Confirm", "positive")
                callBack(eventId, true);
            }
        })
        alert.create()
        alert.show()
    }

    @JavascriptInterface
    fun setStorage(map: String) {
        val map: JSONObject = JSONObject(map)
        val sharedPref = mContext.getSharedPreferences("conping", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(map.getString("key"), map.getString("value"))
            apply()
        }

    }

    @JavascriptInterface
    fun getStorage(key: String, eventId: String) {
        val sharedPref = mContext.getSharedPreferences("conping", Context.MODE_PRIVATE) ?: return
        val defaultValue = ""
        val result = sharedPref.getString(key, defaultValue);
        if (result != null) {
            callBack(eventId, result)
        };
    }

    @JavascriptInterface
    fun copyToClipboard(text: String) {
        Log.d("copyToClipboard", text)
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", text)
        clipboard.setPrimaryClip(clip);
    }

    @JavascriptInterface
    fun launchWeb(map: String) {
        val map: JSONObject = JSONObject(map)
        val browserIntent = Intent(android.content.Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(map.getString("url"))
        mContext.startActivity(browserIntent)
    }

    @JavascriptInterface
    fun getVersion(eventId: String) {
        callBack(eventId, BuildConfig.VERSION_NAME)
    }



}