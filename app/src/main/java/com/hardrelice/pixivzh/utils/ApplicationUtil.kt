package com.hardrelice.pixivzh.utils

import android.app.Activity
import android.content.Context

object ApplicationUtil {
    var ApplicationContext: Context? = null
    var Activity: Activity? = null
    fun initial(context: Context?) {
        ApplicationContext = context
        Activity = context as Activity
    }
}