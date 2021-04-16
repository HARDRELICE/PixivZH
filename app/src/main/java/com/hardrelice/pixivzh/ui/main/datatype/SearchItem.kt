package com.hardrelice.pixivzh.ui.main.datatype

import java.lang.Exception

data class SearchItem(
    var illustId: String = "",
    var title: String = "",
    var userId: String = "",
    var userName: String = "",
    var profileImg: String = "",
    var thumbUrl: String = "",
    var width: Int = 0,
    var height: Int = 0,
    var tags: ArrayList<String> = arrayListOf(),
    var pageCount: Int = 1
)
