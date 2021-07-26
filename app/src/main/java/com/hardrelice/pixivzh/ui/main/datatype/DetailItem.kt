package com.hardrelice.pixivzh.ui.main.datatype

import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.ApplicationUtil

data class DetailItem(
    var pid: String = "",
    var illustTitle: String = "",
    var userId: String = "",
    var userName: String = "",
    var picWidth: Float = 0f,
    var picHeight: Float = 0f,
    var ratio: Float = 0f,
    var thumbUri: String = "",
    var thumbUrl: String = "",
    var profileImg: String = "",
    var loadProfile: Boolean = false,
    var tags: MutableList<*> = mutableListOf<Any>(),
    var pageCount: Int = 1,
    var viewCount: String = "",
    var date: String = "",
    var illustComment: String = ApplicationUtil.ApplicationContext!!.resources.getString(R.string.loading),
)