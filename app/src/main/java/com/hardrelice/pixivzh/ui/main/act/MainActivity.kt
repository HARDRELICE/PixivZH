package com.hardrelice.pixivzh.ui.main.act

import android.app.AlertDialog
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hardrelice.pixiver.UIHandler
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
import com.hardrelice.pixivzh.utils.ApplicationUtil
import com.hardrelice.pixivzh.utils.handler
import com.hardrelice.pixivzh.utils.screenSize
import com.hardrelice.pixivzh.utils.setStatusBarColor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainView, MainPresenter>(), MainView {

    private val titleTabs = ArrayList<CustomTabEntity>()
    private val fragments = ArrayList<Fragment>()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun init() {
        ApplicationUtil.initial(this)
        screenSize = screenSize()
        handler = UIHandler(this)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            window.statusBarColor = resources.getColor(
//                R.color.pixiv_blue_variant_transparent,
//                theme
//            )
//        } else {
//            window.statusBarColor = resources.getColor(R.color.pixiv_blue_variant_transparent)
//        }
        R.color.pixiv_blue.setStatusBarColor(this)
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R -> {
    //            window.decorView.windowInsetsController?.setSystemBarsAppearance(
    //                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
    //                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
    //            )
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
                val frg = fragments.get(position) as BaseFragment
                frg.setData()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        nav_home.setTabData(titleTabs)
        nav_home.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_home.currentItem = position
                val frg = fragments[position] as BaseFragment
                frg.setData()
            }

            override fun onTabReselect(position: Int) {
            }

        })

//        val position = IntArray(2)
//        nav_home.getLocationOnScreen(position)
//        nav_home.postDelayed({println("getLocationOnScreen:${position[0]},${position[1]}")},10000)
//

//        var navHomePosOriginal:Int = -1
//
//        nav_home.post{
//            val position = IntArray(2)
//            nav_home.getLocationOnScreen(position)
//            navHomePosOriginal = position[1]
//        }

//        nav_home.viewTreeObserver.addOnGlobalLayoutListener(object :
//            OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
////                nav_home.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                val rect = Rect()
//                nav_home.getWindowVisibleDisplayFrame(rect)
//                val position = IntArray(2)
//                nav_home.getLocationOnScreen(position)
//                val posY = position[1]
//
//                if (posY == navHomePosOriginal || navHomePosOriginal == -1 || posY == 0) {
//                    nav_home.post { nav_home.visibility = View.VISIBLE }
//                } else {
//                    nav_home.visibility = View.GONE
//                }
//            }
//        })

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