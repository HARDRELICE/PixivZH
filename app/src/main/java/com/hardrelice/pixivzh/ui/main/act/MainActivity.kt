package com.hardrelice.pixivzh.ui.main.act

import android.util.Log
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hardrelice.pixivzh.HttpsUtil
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.base.BaseActivity
import com.hardrelice.pixivzh.mvp.view.BaseFragment
import com.hardrelice.pixivzh.ui.main.adapter.HomeAdapter
import com.hardrelice.pixivzh.ui.main.frg.HomeFragment
import com.hardrelice.pixivzh.ui.main.frg.RankFragment
import com.hardrelice.pixivzh.ui.main.frg.SavedFragment
import com.hardrelice.pixivzh.ui.main.frg.SearchFragment
import com.hardrelice.pixivzh.ui.main.model.TitleModel
import com.hardrelice.pixivzh.ui.main.presenter.MainPresenter
import com.hardrelice.pixivzh.ui.main.view.MainView
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.runBlocking
import java.net.Inet4Address
import java.net.InetAddress
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : BaseActivity<MainView, MainPresenter>(), MainView {

    private val titleTabs = ArrayList<CustomTabEntity>()
    private val fragments = ArrayList<Fragment>()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun init() {
        HttpsUtil.trustEveryone()
        HttpsUtil.disableSSLCertificateChecking()
        ApplicationUtil.initial(this)
        screenSize = screenSize()
        handler = UIHandler(this)
        preference = PreferenceManager.getDefaultSharedPreferences(this)
        if(preference.getString("pixiv_host","").isNullOrEmpty() || preference.getString("pximg_host","").isNullOrEmpty()) {
            thread {
                InetAddress.getByName("pixiv.com").hostAddress.orEmpty().also {
                    preference.edit()
                        .putString("pixiv_host", it)
                        .apply()
                }
                thread {
                    InetAddress.getByName("pximg.net").hostAddress.orEmpty().also {
                        preference.edit()
                            .putString("pximg_host",it)
                            .apply()
                    }
                }.start()
            }.start()
        }
//        property = Property("pixivzh.config", this)

        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R -> {
                val wic = ViewCompat.getWindowInsetsController(window.decorView)
                wic?.isAppearanceLightNavigationBars = false
            }
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M -> {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            else -> {
//                window.statusBarColor = resources.getColor(R.color.white)
            }
        }

        val title = resources.getStringArray(R.array.title)
        val selectIds = resources.obtainTypedArray(R.array.select)
        val unselectIds = resources.obtainTypedArray(R.array.unselect)

        for (i: Int in title.indices) {
            titleTabs.add(
                TitleModel(
                    title[i],
                    selectIds.getResourceId(i, 0),
                    unselectIds.getResourceId(i, 0)
                )
            )
        }

        selectIds.recycle()
        unselectIds.recycle()

        fragments.add(HomeFragment())
        fragments.add(RankFragment())
        fragments.add(SearchFragment())
        fragments.add(SavedFragment())
        vp_home.adapter = HomeAdapter(supportFragmentManager, fragments)
        vp_home.offscreenPageLimit = fragments.size
        vp_home.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                nav_home.currentTab = position
                if (position!=3) {
                    val frg = fragments.get(position) as BaseFragment
                    frg.setData()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        nav_home.setTabData(titleTabs)
        nav_home.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_home.currentItem = position
                if (position!=3) {
                    val frg = fragments[position] as BaseFragment
                    frg.setData()
                }
            }

            override fun onTabReselect(position: Int) {
            }

        })

    }

    override fun initData() {

    }

    override fun createPresenter() = MainPresenter()

    override fun <T> setData(data: T) {
        Log.e("test", "========>$data")
    }

    override fun setError(error: String) {

    }
}