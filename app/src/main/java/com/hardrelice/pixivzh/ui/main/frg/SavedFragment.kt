package com.hardrelice.pixivzh.ui.main.frg

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.hardrelice.pixivzh.R

class SavedFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_saved, rootKey)
//        val cookiesCheck: CheckBoxPreference? = findPreference("use_cookies")
//        val cookiesText: EditTextPreference? = findPreference("cookies")
//        this.activity?.getPreferences(Activity.MODE_PRIVATE)
    }
}