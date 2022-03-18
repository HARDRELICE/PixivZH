package com.hardrelice.pixivzh.ui.main.adapter

import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.ui.main.datatype.DetailItem
import com.hardrelice.pixivzh.ui.main.datatype.ImageItem
import com.hardrelice.pixivzh.utils.*
import com.hardrelice.pixivzh.widget.MultiTypeAdapter
import com.hardrelice.pixivzh.widget.addons.ImageDialogWebViewClient
import kotlinx.android.synthetic.main.dialog_photo_entry.view.*
import java.io.BufferedOutputStream
import java.io.File

class ImageDetailAdapter :
    MultiTypeAdapter() {
    init {
        registerType(ImageItem::class.java, ImagesViewHolder::class.java)
        registerType(DetailItem::class.java, DetailViewHolder::class.java)
    }

    class ImagesViewHolder(private val parent: ViewGroup) : BaseViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.image_view, parent, false)
    ) {
        var downloadHere: Boolean = false
        override fun setData(position: Int, data: Any) {
            val item = data as ImageItem
            if (!item.load) return
            val key = "${item.pid}_$position"
            val img = itemView.findViewById<ImageView>(R.id.main)
            val lp = img.layoutParams
            lp.width = (screenSize[0].toFloat() - 40.dp2px()).toInt()
            lp.height = (lp.width.toFloat() / item.ratio).toInt()
            println(lp.height)
            img.layoutParams = lp
            val imgPath = FileHandler.getIllustFolder(item.pid, "p${item.page}.jpg")
            if (File(imgPath).exists()) {
                handler.post {
                    val drawableCrossFadeFactory =
                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true)
                            .build()
                    Glide.with(handler.activity.applicationContext)
                        .asDrawable()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(imgPath)
                        .transition(
                            DrawableTransitionOptions.with(
                                drawableCrossFadeFactory
                            )
                        )
                        .into(img)
                }
                setSCL(img, item.pid, item.page)
            } else if (DownloadManager.downloadingMap[key] != true) {
                downloadHere = true
                DownloadManager.downloadingMap[key] = true
                Thread {
                    Pixiv.getIllustImage(item.pid, handler, -1, item.page)
                    DownloadManager.downloadingMap[key] = false
                    handler.post {
                        if (position == layoutPosition) {
                            try {
                                val drawableCrossFadeFactory =
                                    DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true)
                                        .build()
                                Glide.with(handler.activity.applicationContext)
                                    .asDrawable()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .load(imgPath)
                                    .transition(
                                        DrawableTransitionOptions.with(
                                            drawableCrossFadeFactory
                                        )
                                    )
                                    .into(img)
                                setSCL(img, item.pid, item.page)
                            } catch (e: Exception) {
                                println(e.message)
                            }
                        }
                    }
                }.also {
                    it.start()
                }
            } else {
                if (!downloadHere) {
                    Thread {
                        while (!File(imgPath).exists()) {
                            Thread.sleep(1000)
                        }
                        handler.post {
                            if (position == layoutPosition) {
                                try {
                                    val drawableCrossFadeFactory =
                                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true)
                                            .build()
                                    Glide.with(handler.activity.applicationContext)
                                        .asDrawable()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .load(imgPath)
                                        .transition(
                                            DrawableTransitionOptions.with(
                                                drawableCrossFadeFactory
                                            )
                                        )
                                        .into(img)
                                    setSCL(img, item.pid, item.page)
                                } catch (e: Exception) {
                                    println(e.message)
                                }
                            }
                        }
                    }.start()
                }
            }
        }

        fun setSCL(view: ImageView, pid: String, page: Int) {
            view.onSingleClick({
                val inflater = LayoutInflater.from(view.context)
                val imgEntryView: View =
                    inflater.inflate(R.layout.dialog_photo_entry, parent, false) // 加载自定义的布局文件
                val dialog = Dialog(getActivity(view)!!, R.style.FullScreenDialog)
                dialog.setContentView(imgEntryView)
                dialog.window?.let {
                    val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                    it.decorView.systemUiVisibility = option
                    it.navigationBarColor = Color.TRANSPARENT
                    it.statusBarColor = Color.TRANSPARENT
                }
//                dialog.window!!.setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//                )

//                dialog.window?.let {
//                    it.decorView.setPadding(0, 0, 0, 0)
//                    it.decorView.background = R.color.transparent.toDrawable()
//                    it.setLayout(screenSize[0], screenSize[1])
//                }
                val data = """
            <!DOCTYPE html>
            <html>
            <head>
            	<title></title>
            	<style type="text/css">
            		.ui-flex,
            		.ui-flex *,
            		.ui-flex :after,
            		.ui-flex :before{
            			box-sizing: border-box;
            			display: flex;
            		}

            		.ui-flex.justify-center {
            			-webkit-box-pack:center;
            			-webkit-justify-content:center;
            			-ms-flex-pack:center;
            			justify-content:center;
            		}

            		.ui-flex.center {
            			-webkit-box-pack:center;
            			-webkit-justify-content:center;
            			-ms-flex-pack:center;
            			justify-content:center;
            			-webkit-box-align:center;
            			-webkit-align-items:center;
            			-ms-flex-align:center;
            			align-items:center;
            		}
            	</style>
            </head>
            <body style="padding: 0;margin: 0;background-color:black;">
            		<div class="ui-flex justify-center center" style="width: 100%;height: ${screenSize[1]}dpi;">
            				<img  src="file:///${
                    FileHandler.getIllustFolder(
                        pid,
                        "p$page.jpg"
                    )
                }" style="width:100%;">
            		</div>
            </body>
            <script type="text/javascript">
            	//var pic = document.getElementsByTagName("img")[0].height
                //var win = window.innerHeight
                //var height = pic>win?pic:win
	            //document.body.getElementsByClassName("ui-flex")[0].setAttribute("style","width:100%;height:"+height+"px")
                window.onload = function(){
                    img = document.getElementsByTagName("div")[0]
                    if (img.children[0].height<window.innerHeight) {
                        img.setAttribute("style","width:100%;height:"+window.innerHeight+"px")
                    } else {
                        img.setAttribute("style","width:100%;")
                    }
                }
            </script>
            </html>
            """.trimIndent()
                dialog.setCanceledOnTouchOutside(true)
                dialog.setOnCancelListener {
                    println("Dialog Canceled!")
                }
                dialog.show()
                val img = imgEntryView.large_image
                img.webViewClient = ImageDialogWebViewClient(img)
                handler.post {
                    img.settings.setSupportZoom(true)
                    img.settings.useWideViewPort = true
                    img.settings.builtInZoomControls = true
                    img.settings.displayZoomControls = false
                    img.settings.javaScriptEnabled = true
                    img.settings.allowFileAccess = true
                    img.settings.allowContentAccess = true
                    img.setInitialScale(25)
                    img.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null)
                }

                img.setOnLongClickListener {
                    FileHandler.writeFileToStorage(
                        FileHandler.getIllustFolder(pid, "p$page.jpg"),
                        "Pictures/pixivzh",
                        "$pid-$page.jpg"
                    )
                    FileHandler.refreshMediaStore(FileHandler.storage("Pictures/pixivzh/$pid-$page.jpg"))
                    Toast.makeText(
                        ApplicationUtil.ApplicationContext,
                        ApplicationUtil.ApplicationContext!!.resources.getString(R.string.saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

            })
        }
    }

    class DetailViewHolder(parent: ViewGroup) : BaseViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.image_detail, parent, false)
    ) {
        override fun setData(position: Int, data: Any) {
            val item = data as DetailItem
            // val  = itemView.findViewById<>(R.id.linear_layout_image_detail)
//            val loadMoreButton = itemView.findViewById<TextView>(R.id.load_more_button)
            val viewCount = itemView.findViewById<TextView>(R.id.text_view_image_detail_view_count)
            val date = itemView.findViewById<TextView>(R.id.text_view_image_detail_date)
            val userProfile =
                itemView.findViewById<ImageButton>(R.id.image_button_image_detail_user_profile)
            val userName = itemView.findViewById<TextView>(R.id.text_view_image_detail_user_name)
            val pid = itemView.findViewById<TextView>(R.id.text_view_image_detail_pid)
            val uid = itemView.findViewById<TextView>(R.id.text_view_image_detail_uid)
            val title = itemView.findViewById<TextView>(R.id.text_view_image_detail_title)
            val tags = itemView.findViewById<TextView>(R.id.text_view_image_detail_tags)
            val comment = itemView.findViewById<WebView>(R.id.web_view_image_detail_comment)

            if (item.viewCount.isNotEmpty() && item.date.isNotEmpty()) {
                viewCount.text = item.viewCount
                date.text = item.date
            }
            userName.text = item.userName
            userName.text = item.userName
            pid.text = item.pid
            uid.text = item.userId
            title.text = item.illustTitle
            tags.text = item.tags.toString()

            // webview comment
            handler.post {
                comment.settings.defaultTextEncodingName = "UTF -8"
                comment.loadDataWithBaseURL(
                    null,
                    item.illustComment,
                    "text/html",
                    "charset=UTF-8",
                    null
                )
            }

            // user profile
            if (item.loadProfile) {
                Log.e("Profile", "Loading")
                val profileUri = FileHandler.getUserFolder(data.userId, "profile.jpg")
                if (File(profileUri).exists()) {
                    handler.post {
                        Glide.with(handler.activity.applicationContext).load(profileUri).into(
                            userProfile
                        )
                    }
                } else if (DownloadManager.downloadingMap[profileUri] != true) {
                    Thread {
                        DownloadManager.downloadingMap[profileUri] = true
                        Log.e("pi", item.profileImg)
                        val tempUri = "$profileUri.temp"
                        Requests.download(
                            item.profileImg.replace("i.pximg.net", pximg_host()),
                            pixiv_headers,
                            tempUri
                        )
                        File(tempUri).inputStream().use { input ->
                            BufferedOutputStream(File(profileUri).outputStream()).use { output ->
                                input.copyTo(output)
                            }
                        }
                        File(tempUri).delete()
                        DownloadManager.downloadingMap[profileUri] = false
                        handler.post {
                            Glide.with(handler.activity.applicationContext).load(profileUri).into(
                                userProfile
                            )
                        }
                    }.let {
                        it.isDaemon = true
                        it.start()
                    }
                }
            }
        }
    }
}