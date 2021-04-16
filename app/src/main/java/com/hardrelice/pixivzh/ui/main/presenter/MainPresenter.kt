package com.hardrelice.pixivzh.ui.main.presenter

import com.hardrelice.pixivzh.mvp.presenter.BasePresenter
import com.hardrelice.pixivzh.ui.main.view.MainView

class MainPresenter: BasePresenter<MainView>() {
    fun getTest(str:String){
        getBaseView()!!.setData(str)
    }
}