package com.hardrelice.pixivzh.utils

object URLProtector {
    fun String.isLegal():Boolean{
        val s = Regex("""\s*""")
        if(s.matchEntire(this)?.value==this){
            return false
        }
        return true
    }
}