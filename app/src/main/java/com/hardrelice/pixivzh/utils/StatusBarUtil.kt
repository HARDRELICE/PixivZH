package com.hardrelice.pixivzh.utils

import android.app.ActionBar
import android.app.Activity
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*

object StatusBarUtil {

    fun hideStatusBar(activity: Activity){
        activity.window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
    }

    fun showStatusBar(activity: Activity){
        activity.window.clearFlags(FLAG_FULLSCREEN)
    }
    fun Int.setStatusBarColor(activity: Activity){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activity.window.statusBarColor = activity.resources.getColor(
                this,
                activity.theme
            )
        } else {
            activity.window.statusBarColor = activity.resources.getColor(this)
        }
    }
}