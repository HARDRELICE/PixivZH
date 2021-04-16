package com.hardrelice.pixivzh.mvp.view

interface BaseView {
    fun <T> setData(data: T)
    fun setError(error: String)
}