package com.dj.conping

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.net.URLEncoder


var CALLBACK_FUNCTION = "conpingCallback"

class WebAppInterface// Instantiate the interface and set the context
internal constructor(internal var mContext: Activity, var webView: WebView, var intent: Intent) {
    val backKeyHandler = BackKeyHandler(mContext);

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

    @JavascriptInterface
    fun callShare(map: String, eventId: String) {
        val map: JSONObject = JSONObject(map)
        var link = URLEncoder.encode("https://conping.page.link?type=" + map.getString("type") + "&id=" + map.getString("id"), "UTF-8")
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            longLink = Uri.parse("https://conping.page.link/?link=" + link + "&apn=com.dj.conping")
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val link = shortLink.toString().replace("https://conping.page.link/","https://conping-yqoln5urha-an.a.run.app/share/")
            Log.d("shortLink", link)
            callBack(eventId, link)
            sendIntent(link)
        }.addOnFailureListener {
        }
    }

    @JavascriptInterface
    fun onAndroidExit() {
        backKeyHandler.onBackPressed("\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다", 1)
    }


    @JavascriptInterface
    fun onInitialized() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(mContext) { pendingDynamicLinkData ->
                if (pendingDynamicLinkData != null) {
                    val url = Uri.parse(pendingDynamicLinkData.link.toString())
                    Log.d("initFirebaseDeeplink", url.toString())
                    val type = url.getQueryParameter("type").toString();
                    val id = url.getQueryParameter("id").toString()
                    onMessageFromApp(webView, "onDynamicLinkMessage", type, id)
                }
            }
            .addOnFailureListener(mContext) { e -> Log.w("firebase", "getDynamicLink:onFailure", e) }
    }

    private fun onMessageFromApp(webView: WebView, eventName: String, type: String, id: String) {
        Log.d("onMessageFromApp2", "javascript:onMessageFromApp('"+eventName+"', {type: '" + type + "', id: '" + id + "'})")
        webView.loadUrl("javascript:onMessageFromApp('"+eventName+"', {type: '" + type + "', id: '" + id + "'})")
    }

    private fun sendIntent(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        mContext.startActivity(shareIntent)
    }


}