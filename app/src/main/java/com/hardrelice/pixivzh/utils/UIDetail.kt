package com.hardrelice.pixiver

import android.app.Activity
import android.view.View

data class UIDetail (
    var id: Int = -1,
    var int: Int = 0,
    var float: Float = 0F,
    var string: String = "",
    var view: View? = null,
    var obj: Any? = null,
    var activity: Activity? = null,
    var intList: MutableList<Int> = mutableListOf(0, 0),
    var runnable: Runnable? = null
)


