package com.hardrelice.pixivzh.ui.main.act

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.edit
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
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
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
//                ViewCompat.getWindowInsetsController(window.decorView)?.let {
//                    it.isAppearanceLightNavigationBars = false
//                    it.hide(WindowInsetsCompat.Type.systemBars())
//                    it.systemBarsBehavior =
//                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                    it.isAppearanceLightStatusBars = true
//
//                }
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
////                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//            else -> {
////                window.statusBarColor = resources.getColor(R.color.white)
//            }
//        }

        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = option
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT

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
                    val frg = fragments[position] as BaseFragment
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