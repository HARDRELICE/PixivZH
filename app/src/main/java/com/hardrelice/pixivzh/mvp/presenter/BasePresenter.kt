package com.hardrelice.pixivzh.mvp.presenter

open class BasePresenter<V> {
    private var mBaseView: V? = null
    fun bindView(view: V) {
        this.mBaseView = view
    }

    fun unBindView() {
        this.mBaseView = null
    }

    fun getBaseView()= mBaseView
}