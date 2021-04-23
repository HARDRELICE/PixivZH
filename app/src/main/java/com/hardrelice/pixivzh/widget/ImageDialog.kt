package com.hardrelice.pixivzh.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.StatusBarUtil.hideStatusBar
import com.hardrelice.pixivzh.utils.StatusBarUtil.setStatusBarColor
import com.hardrelice.pixivzh.utils.StatusBarUtil.showStatusBar

class ImageDialog(activity: Activity, theme: Int): Dialog(activity, theme) {
    val activity = activity
    override fun show() {
        hideStatusBar(activity)
//        R.color.black.setStatusBarColor(activity)
        super.show()
    }
    override fun cancel() {
        showStatusBar(activity)
//        R.color.pixiv_blue.setStatusBarColor(activity)
        super.cancel()
    }
}