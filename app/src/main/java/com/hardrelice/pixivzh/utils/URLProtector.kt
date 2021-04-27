package com.hardrelice.pixivzh.utils

import androidx.core.net.toUri
import java.net.URL
import java.net.URLEncoder

object URLProtector {
    fun String.isURLLegal():Boolean{
        val s = Regex("""\s*""")
        if(s.matchEntire(this)?.value==this){
            return false
        }
        return true
    }
    fun String.encodeURL():String{
        return URLEncoder.encode(this, "utf-8")
    }
}

