package com.dj.conping

import android.app.Activity
import android.os.Handler
import android.util.Log
import android.widget.Toast

class BackKeyHandler
internal constructor(mContext: Activity) {
    private var backKeyPressedTime: Long = 0
    private var activity: Activity? = mContext
    private var toast: Toast? = null


    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity!!.finish()
            toast!!.cancel()
        }
    }

    fun onBackPressed(msg: String) {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide(msg)
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity!!.finish()
            toast!!.cancel()
        }
    }

    fun onBackPressed(time: Double) {
        if (System.currentTimeMillis() > backKeyPressedTime + time * 1000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity!!.finish()
            toast!!.cancel()
        }
    }

    fun onBackPressed(msg: String, time: Int) {
        if (System.currentTimeMillis() > backKeyPressedTime + (time * 1000)) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide(msg, time)
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity!!.finish()
            toast!!.cancel()
        }
    }

    private fun showGuide() {
        val mToastToShow = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
        mToastToShow.show()
        Handler().postDelayed({
            mToastToShow.cancel()
        }, 1000)
    }

    private fun showGuide(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showGuide(msg: String, time: Int) {
        val mToast = Toast.makeText(activity, msg, Toast.LENGTH_LONG)
        mToast.show()
        Handler().postDelayed({
            mToast.cancel()
        }, 1000)
    }
}