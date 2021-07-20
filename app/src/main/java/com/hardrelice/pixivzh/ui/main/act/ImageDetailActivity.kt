package com.hardrelice.pixivzh.ui.main.act

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixivzh.utils.UIHandler
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.utils.Pixiv
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.*
import com.hardrelice.pixivzh.widget.ImageDialog
import com.hardrelice.pixivzh.widget.addons.ImageDialogWebViewClient
import kotlinx.android.synthetic.main.activity_image_detail.*
import kotlinx.android.synthetic.main.activity_image_detail.view.*
import kotlinx.android.synthetic.main.dialog_photo_entry.view.*
import java.io.File

class ImageDetailActivity : Activity() {
    var handler = UIHandler(this)
    lateinit var firstView: ImageView
    var downloaded = hashMapOf<Int, Boolean>()
    var downloading = hashMapOf<Int, Boolean>()

    lateinit var illustId: String
    lateinit var illustTitle: String
    lateinit var userId: String
    lateinit var userName: String
    var picWidth: Float = 0f
    var picHeight: Float = 0f
    var ratio: Float = 1f
    lateinit var thumbUri: String
    lateinit var thumbUrl: String
    lateinit var profileImg: String
    lateinit var tags: MutableList<*>
    var pageCount: Int = 0
    lateinit var originalUri: String

    fun setImage(
        view: ImageView,
        pid: String,
        uri: String,
        ratio: Float,
        progressBarId: Int,
        thumbUrl: String,
        page:Int = 0
    ) {
        if (downloading[page] == true) return
        downloading[page] = true
        handler.post {
            view.setImageResource(R.color.pixiv_blue)
        }
        downloaded[page] = Pixiv.getIllustImage(pid, handler, progressBarId, page)
        if (downloaded[page] == true) {
            val msg = Message()
            msg.what = UIHandler.SET_IMAGE
            val detail = UIDetail(view = view, string = uri, float = ratio)
            msg.obj = detail
            handler.sendMessage(msg)
        } else {
            handler.toast("downloading failed...")
            handler.post {
                view.setImageResource(R.color.inactive_grey)
            }
        }
        downloading[page] = false
    }

    fun setInfo(pid: String) {
        handler.post {
            progress_bar_image_detail_get_info.visibility = View.VISIBLE
            web_view_image_detail_comment.settings.defaultTextEncodingName = "UTF -8"
            web_view_image_detail_comment.loadDataWithBaseURL(
                null,
                "Loading…",
                "text/html",
                "charset=UTF-8",
                null
            )
        }

        val info = Pixiv.getIllustInfo(pid)
        if (info != null) {
            handler.post {
                progress_bar_image_detail_get_info.visibility = View.INVISIBLE
                web_view_image_detail_comment.loadDataWithBaseURL(
                    null,
                    info.illustComment,
                    "text/html",
                    "charset=UTF-8",
                    null
                )
                web_view_image_detail_comment.scrollY = WebView.SCROLL_AXIS_NONE
                text_view_image_detail_date.text = info.createDate
                "${info.viewCount} views".also { text_view_image_detail_view_count.text = it }
            }
            //progress_bar_image_detail.id
            setImage(
                firstView,
                illustId,
                originalUri,
                ratio,
                progress_bar_image_detail.id,
                thumbUrl
            )
            handler.post {
                setSCL(firstView, 0)
            }
            loadMoreImage(info, pid)
        }
    }

    fun loadMoreImage(info: Pixiv.IllustInfo, pid: String) {
        if (info.pageCount > 1) {
            Log.e("PAGECOUNT>1!!!!!!!!!", info.pageCount.toString())
            val url = info.urls?.original as String
            val imageViews = hashMapOf<Int, ImageView>()
            for (i in 1 until info.pageCount) {
                val imageView = ImageView(this)
                val lp =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, firstView.height)
                handler.post {
                    imageView.layoutParams = lp
                    imageView.setImageResource(R.color.pixiv_blue)
                    moreImageLayout.addView(imageView)
                }
                imageViews[i] = imageView
            }
            for (i in 1 until info.pageCount){
                val picPath = FileHandler.getIllustFolder(pid, "p$i.jpg")
                setImage(imageViews[i]!!, pid, picPath, ratio,-1, "", i)
                Log.e("page","$i")
                handler.post {
                    setSCL(imageViews[i]!!, i)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setSCL(view: ImageView, page: Int){
        view.onSingleClick({
            if (downloaded[page]!=true) return@onSingleClick
            val inflater = LayoutInflater.from(this)
            val imgEntryView: View =
                inflater.inflate(R.layout.dialog_photo_entry, null) // 加载自定义的布局文件
            val dialog = ImageDialog(this, R.style.FullScreenDialog)
            dialog.setContentView(imgEntryView)
            dialog.window!!.setBackgroundDrawableResource(R.color.black)
            dialog.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            dialog.window!!.decorView.setPadding(0, 0, 0, 0)
            dialog.window!!.decorView.background = R.color.transparent.toDrawable()
            dialog.window!!.setLayout(screenSize[0], screenSize[1])
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
            				<img  src="file:///${FileHandler.getIllustFolder(illustId, "p$page.jpg")}" style="width:100%;">
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
                FileHandler.writeFileToStorage(FileHandler.getIllustFolder(illustId, "p$page.jpg"), "Pictures/pixivzh", "$illustId-$page.jpg")
                FileHandler.refreshMediaStore(FileHandler.storage("Pictures/pixivzh/$illustId-$page.jpg"))
                Toast.makeText(ApplicationUtil.ApplicationContext, "Saved!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnLongClickListener true
            }
            imgEntryView.setOnClickListener {
                dialog.cancel()
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println(intent.extras?.getString("user_name"))
        illustId = intent.extras?.getString("illust_id") as String
        illustTitle = intent.extras?.getString("title") as String
        userId = intent.extras?.getString("user_id") as String
        userName = intent.extras?.getString("user_name") as String
        picWidth = intent.extras?.getFloat("width") as Float
        picHeight = intent.extras?.getFloat("height") as Float
        ratio = picWidth / picHeight
        thumbUri = intent.extras?.getString("uri") as String
        thumbUrl = intent.extras?.getString("thumb_url") as String
        profileImg = intent.extras?.getString("profile_img") as String
        tags = intent.extras?.getStringArrayList("tags") as MutableList<*>
        pageCount = intent.extras?.getInt("page_count") as Int
        originalUri = FileHandler.getIllustFolder(illustId, "p0.jpg")

        setContentView(R.layout.activity_image_detail)

        toolbar_image_detail.setBackgroundColor(R.color.pixiv_blue.getColor())

        toolbar_image_detail.setNavigationOnClickListener {
            this.finish()
        }
//        floating_action_button_save_image.setOnClickListener {
//            if (downloaded && File(originalUri).exists()) {
//                FileHandler.writeFileToStorage(originalUri, "Pictures/pixivzh", "$illustId.jpg")
//                FileHandler.refreshMediaStore(FileHandler.storage("Pictures/pixivzh/$illustId.jpg"))
//                Toast.makeText(ApplicationUtil.ApplicationContext, "Saved!", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Downloading...",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Thread {
//                    setImage(
//                        view,
//                        illustId,
//                        originalUri,
//                        ratio,
//                        progress_bar_image_detail.id,
//                        thumbUrl
//                    )
//                }.let {
//                    it.isDaemon = true
//                    it.start()
//                }
//            }
//        }

        firstView = findViewById(R.id.image_view_image_detail)
        firstView.setImageResource(R.color.pixiv_blue)
        var width = 0
        var height = 0
        firstView.post {
            width = firstView.width
            height = (width / ratio).toInt()
            firstView.layoutParams.height = height
            Log.e("Width", width.toString())
        }
        toolbar_image_detail.title = illustTitle
        toolbar_image_detail.subtitle = userName
        text_view_image_detail_title.text = illustTitle
        text_view_image_detail_pid.text = illustId
        text_view_image_detail_uid.text = userId
        text_view_image_detail_user_name.text = userName
        text_view_image_detail_tags.text = tags.toString()
        Thread {
            val profileUri = FileHandler.getUserFolder(userId, "profile.jpg")
            if (!File(profileUri).exists()) {
                Requests.download(
                    profileImg.replace("i.pximg.net", pximg_host),
                    pixiv_headers,
                    profileUri
                )
            }
            handler.post {
                Glide.with(applicationContext).load(profileUri).into(
                    image_button_image_detail_user_profile
                )
            }
        }.let {
            it.isDaemon = true
            it.start()
        }

//        Thread {
//            setImage(
//                view,
//                illustId,
//                originalUri,
//                ratio,
//                progress_bar_image_detail.id,
//                thumbUrl
//            )
//        }.let {
//            it.isDaemon = true
//            it.start()
//        }
        Thread {
            setInfo(illustId)
        }.let {
            it.isDaemon = true
            it.start()
        }
    }
}