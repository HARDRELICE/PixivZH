package com.hardrelice.pixivzh.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ApplicationUtil {
    var ApplicationContext: Context? = null
    var Activity: Activity? = null
    fun initial(@SuppressLint("StaticFieldLeak") context: Context?) {
        ApplicationContext = context
        Activity = context as Activity
    }
}