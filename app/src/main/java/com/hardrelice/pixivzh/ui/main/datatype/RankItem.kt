package com.hardrelice.pixivzh.ui.main.datatype

import android.icu.text.CaseMap

data class RankItem(
    var illustId: String = "",
    var title: String = "",
    var rank: String = "",
    var userId: String = "",
    var userName: String = "",
    var profileImg: String = "",
    var thumbUrl: String = "",
    var width: Int = 0,
    var height: Int = 0,
    var tags: ArrayList<String> = arrayListOf(),
    var pageCount: Int = 1
)
