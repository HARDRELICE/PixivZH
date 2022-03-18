package com.hardrelice.pixivzh.ui.main.frg

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.preference.*
//import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.handler
import com.hardrelice.pixivzh.utils.preference
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


        object : Thread() {
            override fun run() {
                InetAddress.getByName("pixiv.com").hostAddress
                    .orEmpty().also {
                        handler.post {
                            preference.edit()
                                .putString("pixiv_host", it)
                                .apply()
                        }
                    }

                InetAddress.getByName("pximg.net").hostAddress
                    .orEmpty().also {
                        handler.post {
                            preference.edit()
                                .putString("pximg_host", it)
                                .apply()
                        }
                    }
            }
        }.start()
//        thread(true,true,null,null,-1) {
//
//        }
    }
}