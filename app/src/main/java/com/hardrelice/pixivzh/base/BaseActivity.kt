package com.hardrelice.pixivzh.base

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.hardrelice.pixivzh.mvp.presenter.BasePresenter
import com.hardrelice.pixivzh.mvp.view.BaseView

abstract class BaseActivity<V, P : BasePresenter<V>> : FragmentActivity(), BaseView {
    private var mPresenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        if (mPresenter == null) {
            mPresenter = createPresenter() as P
        }
        mPresenter!!.bindView(this as V)
        init()
        initData()
    }

    protected abstract fun getLayoutId():Int

    protected abstract fun init()

    protected abstract fun initData()

    protected abstract fun createPresenter():P

    fun getPresenter() = mPresenter

    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.unBindView()
    }

}