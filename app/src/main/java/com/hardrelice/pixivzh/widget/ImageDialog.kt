package com.hardrelice.pixivzh.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.setStatusBarColor

class ImageDialog(activity: Activity, theme: Int): Dialog(activity, theme) {
    val activity = activity
    override fun show() {
        R.color.black.setStatusBarColor(activity)
        super.show()
    }
    override fun cancel() {
        R.color.pixiv_blue.setStatusBarColor(activity)
        super.cancel()
    }
}