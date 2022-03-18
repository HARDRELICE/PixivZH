package com.hardrelice.pixivzh.ui.main.act

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.ui.main.adapter.ImageDetailAdapter
import com.hardrelice.pixivzh.ui.main.datatype.DetailItem
import com.hardrelice.pixivzh.ui.main.datatype.ImageItem
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.activity_image_detail.*


class ImageDetailActivity : Activity() {
    private var infoSet: Boolean = false
    private var loadedMore: Boolean = preference.getBoolean("auto_load_more_pics", false)
    private var moreImageItems = mutableListOf<ImageItem>()
    lateinit var detailItem: DetailItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

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

        val picWidth = intent.extras?.getFloat("width") as Float
        val picHeight = intent.extras?.getFloat("height") as Float
        detailItem = DetailItem(
            pid = intent.extras?.getString("illust_id") as String,
            illustTitle = intent.extras?.getString("title") as String,
            userId = intent.extras?.getString("user_id") as String,
            userName = intent.extras?.getString("user_name") as String,
            picWidth = picWidth,
            picHeight = picHeight,
            ratio = picWidth / picHeight,
            thumbUri = intent.extras?.getString("uri") as String,
            thumbUrl = intent.extras?.getString("thumb_url") as String,
            profileImg = intent.extras?.getString("profile_img") as String,
            tags = intent.extras?.getStringArrayList("tags") as MutableList<*>,
            pageCount = intent.extras?.getInt("page_count") as Int,
        )

        toolbar_image_detail.setBackgroundColor(R.color.pixiv_blue.getColor())
        toolbar_image_detail.setNavigationOnClickListener { this.finish() }
        toolbar_image_detail.title = detailItem.illustTitle
        toolbar_image_detail.subtitle = detailItem.userName

        recycler_view_image_detail.setHasFixedSize(false)
        recycler_view_image_detail.layoutManager = LinearLayoutManager(this)
        val adapter = ImageDetailAdapter()
        recycler_view_image_detail.setItemViewCacheSize(3)

        val firstImageImageItem = ImageItem(
            true,
            0,
            detailItem.pid,
            detailItem.ratio,
            detailItem.thumbUrl
        )
        for (i in 1 until detailItem.pageCount) {
            moreImageItems.add(
                ImageItem(
                    true,
                    page = i,
                    detailItem.pid,
                    detailItem.ratio,
                    detailItem.thumbUrl
                )
            )
        }

        adapter.arrayList.add(firstImageImageItem)
        if (loadedMore) {
            floating_action_button_load_more_image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            adapter.arrayList.addAll(1, moreImageItems)
        }
        adapter.arrayList.add(detailItem)

        println(adapter.arrayList)
        recycler_view_image_detail.adapter = adapter
        adapter.notifyDataSetChanged()
        Thread {
            setInfo(adapter)
        }.also {
            it.isDaemon = true
            it.start()
        }

        floating_action_button_load_more_image.onSingleClick({
            if (!infoSet) return@onSingleClick
            if (loadedMore) {
                loadedMore = !loadedMore
                floating_action_button_load_more_image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                adapter.removeItems(1, moreImageItems.size)
            } else {
                loadedMore = !loadedMore
                floating_action_button_load_more_image.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                adapter.addItems(1, moreImageItems)
            }
        })

        Log.e("PAGECOUNT", detailItem.pageCount.toString())
    }

    private fun setInfo(adapter: ImageDetailAdapter) {
        progress_bar_image_detail_get_info.visibility = View.VISIBLE
        var info: Pixiv.IllustInfo? = null
        while (info == null) {
            info = Pixiv.getIllustInfo(detailItem.pid)
        }

        handler.post {
            progress_bar_image_detail_get_info.visibility = View.INVISIBLE
            detailItem.viewCount = info.viewCount.toString()
            detailItem.date = info.createDate
            detailItem.illustComment = info.illustComment
            detailItem.loadProfile = true
            adapter.notifyItemChanged(adapter.itemCount - 1)
//            adapter.replaceItem(adapter.itemCount - 1, detailItem)
        }
        infoSet = true
        if (info.pageCount > 1) handler.post {
            floating_action_button_load_more_image.visibility = View.VISIBLE
        }
    }
}