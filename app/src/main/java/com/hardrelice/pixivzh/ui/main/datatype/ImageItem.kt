package com.hardrelice.pixivzh.ui.main.datatype

data class ImageItem(
    var load: Boolean = false,
    var page: Int = 0,
    var pid: String = "",
    var ratio: Float = -1f,
    var thumbUrl: String = ""
)
