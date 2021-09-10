package com.hardrelice.pixivzh.ui.main.adapter

import android.preference.PreferenceFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.prefs.PreferenceChangeEvent

class HomeAdapter(fm:FragmentManager, private val fragments: ArrayList<Fragment>):FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = fragments.size
    override fun getItem(position: Int): Fragment = fragments[position]
}