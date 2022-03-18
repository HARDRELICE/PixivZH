package com.hardrelice.pixivzh.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixivzh.ui.main.adapter.RankAdapter
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.abs


class UIHandler(activity: Activity) : Handler(Looper.getMainLooper()) {
    private var mActivity: WeakReference<Activity> = WeakReference(activity)
    var activity = activity

    companion object {
        const val RUN_ANY: Int = 0
        const val UPDATE_PROGRESS_BAR: Int = 1
        const val SET_IMAGE: Int = 2
        const val SET_IMAGE_VIEW: Int = 3
        const val SET_FRAGMENT: Int = 4
        const val UPDATE_FRAGMENT: Int = 5
    }

    fun send(what: Int, detail: UIDetail) {
        val msg = Message()
        msg.what = what
        msg.obj = detail
        this.sendMessage(msg)
    }

    fun updateProgressBar(viewId: Int, progress: Int) {
        val progressBar: ProgressBar = activity.findViewById(viewId)
        if (abs(progress - progressBar.progress) > 1 || progress == 100 || progress == 0) {
            progressBar.progress = progress
//            println(progressBar.progress)
        }
    }

    fun setImage(view: ImageView, imgPath: String, ratio: Float) {
        try {
            if(!File(imgPath).exists()) {
                return
            }
            val screenWidth: Int = view.width
            val lp: ViewGroup.LayoutParams = view.layoutParams
            lp.width = screenWidth
            lp.height = (screenWidth / ratio).toInt()
            println("height ${lp.height}")
//                ViewGroup.LayoutParams.WRAP_CONTENT
            view.layoutParams = lp
            val drawableCrossFadeFactory =
                DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(activity.applicationContext)
                .asDrawable()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(imgPath)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(view)
        } catch (e: Exception) {
            Log.e("SetImage", "Glide exception")
        }
    }

    fun setImageView(view: View, imgPath: String) {
        val illustView = view as ImageView
        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 500
        try {
            illustView.setImageURI(imgPath.toUri())
            illustView.startAnimation(animation)
        } catch (e: Exception) {
            Log.e("SetImage", "glide exception")
        }
    }

    fun toast(text:String,duration:Int=Toast.LENGTH_SHORT){
        this.post{
            Toast.makeText(ApplicationUtil.ApplicationContext,text,duration).show()
        }
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val uiDetail: UIDetail = msg.obj as UIDetail
        when (msg.what) {
            RUN_ANY -> {
                post {
                    uiDetail.runnable?.run()
                }
            }
            UPDATE_PROGRESS_BAR -> {
                post {
                    val percent: Int
                    if (uiDetail.int != 0) {
                        percent = uiDetail.int
                    } else {
                        percent = uiDetail.float.toInt()
                    }
                    updateProgressBar(uiDetail.id, percent)
                }
            }
            SET_IMAGE -> {
                post {
                    setImage(uiDetail.view!! as ImageView, uiDetail.string, uiDetail.float)
                }
            }
            SET_IMAGE_VIEW -> {
                post {
                    setImageView(uiDetail.view!!, uiDetail.string)
                }
            }
            SET_FRAGMENT -> {
                post {
                    val view = uiDetail.view as RecyclerView
                    val rank = uiDetail.obj as List<RankItem>
                    view.adapter =
                        RankAdapter(rank, uiDetail.activity as FragmentActivity)
                }
            }
            UPDATE_FRAGMENT -> {
                post {
                    val view = uiDetail.view as RecyclerView
                    val rank = uiDetail.obj as List<RankItem>
                    val adapter = view.adapter as RankAdapter
                    adapter.addRangeItem(uiDetail.int, rank.size, rank)
                    view.adapter!!.notifyItemRangeInserted(uiDetail.int, rank.size)
                }
            }


        }
    }
}