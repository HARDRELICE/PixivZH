package com.hardrelice.pixivzh.ui.main.frg

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.preference.*
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.handler
import com.hardrelice.pixivzh.utils.preference
import kotlinx.coroutines.runBlocking
import java.net.InetAddress
import kotlin.concurrent.thread

class SavedFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_saved, rootKey)
        val refresh = findPreference<PreferenceScreen>("REFRESH_HOST")
        refresh?.setOnPreferenceClickListener {
            refreshHost()
            true
        }
    }

    fun refreshHost() {
        handler.post {
            thread {
                InetAddress.getByName("pixiv.com").hostAddress.orEmpty().also {
                    preference.edit()
                        .putString("pixiv_host", it)
                        .apply()
                }
                thread {
                    InetAddress.getByName("pximg.net").hostAddress.orEmpty().also {
                        preference.edit()
                            .putString("pximg_host", it)
                            .apply()
                    }
                }.start()
            }.start()
        }
    }

}