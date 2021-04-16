package com.hardrelice.pixivzh.ui.main.act

import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixiver.UIHandler
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.Pixiv
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.Requests
import com.hardrelice.pixivzh.utils.*
import com.hardrelice.pixivzh.widget.ImageDialog
//import com.jelly.mango.Mango
//import com.jelly.mango.Mango.position
//import com.jelly.mango.MultiplexImage
import kotlinx.android.synthetic.main.activity_image_detail.*
import kotlinx.android.synthetic.main.dialog_photo_entry.view.*
import java.io.File


class ImageDetailActivity : Activity() {
    var handler = UIHandler(this)
    lateinit var view: ImageView
    var downloaded = false
    var downloading = false
    fun setImage(
        view: View,
        pid: String,
        uri: String,
        ratio: Float,
        progressBarId: Int,
        thumbUrl: String
    ) {
        if (downloading) return
        downloading = true
        downloaded = Pixiv.getIllustImage(pid, handler, progressBarId)
        val msg = Message()
        msg.what = UIHandler.SET_IMAGE
        val detail = UIDetail(view = view, string = uri, float = ratio)
        msg.obj = detail
        handler.sendMessage(msg)
        downloading = false
    }

    fun setInfo(pid: String) {
        var runnable = Runnable {
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
        val detail = UIDetail(runnable = runnable)
        handler.send(
            UIHandler.RUN_ANY,
            detail
        )
        val info = Pixiv.getIllustInfo(pid)
        if (info != null) {
            runnable = Runnable {
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
                text_view_image_detail_view_count.text = "${info.viewCount} views"
            }
            val detail = UIDetail(runnable = runnable)
            handler.send(
                UIHandler.RUN_ANY,
                detail
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println(intent.extras?.getString("user_name"))
        val illustId = intent.extras?.getString("illust_id") as String
        val illustTitle = intent.extras?.getString("title") as String
        val userId = intent.extras?.getString("user_id") as String
        val userName = intent.extras?.getString("user_name") as String
        val picWidth = intent.extras?.getFloat("width") as Float
        val picHeight = intent.extras?.getFloat("height") as Float
        val ratio = picWidth/picHeight
        val thumbUri = intent.extras?.getString("uri") as String
        val thumbUrl = intent.extras?.getString("thumb_url") as String
        val profileImg = intent.extras?.getString("profile_img") as String
        val tags = intent.extras?.getStringArrayList("tags") as MutableList<*>
        val pageCount = intent.extras?.getInt("page_count") as Int
        println(thumbUrl)
        val originalUri = FileHandler.getIllustFolder(illustId as String, "original.jpg")

        setContentView(R.layout.activity_image_detail)

        R.color.pixiv_blue.setStatusBarColor(this)
        toolbar_image_detail.setBackgroundColor(R.color.pixiv_blue.getColor())

        toolbar_image_detail.setNavigationOnClickListener {
            this.finish()
        }
        floating_action_button_save_image.setOnClickListener {
            if (downloaded) {
                FileHandler.writeFileToStorage(originalUri, "Pictures/pixivzh", "$illustId.jpg")
                FileHandler.refreshMediaStore(FileHandler.storage("Pictures/pixivzh/$illustId.jpg"))
                Toast.makeText(ApplicationUtil.ApplicationContext, "Saved!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    ApplicationUtil.ApplicationContext,
                    "Downloading...",
                    Toast.LENGTH_SHORT
                ).show()
                Thread {
                    setImage(
                        view,
                        illustId,
                        originalUri,
                        ratio,
                        progress_bar_image_detail.id,
                        thumbUrl
                    )
                }.start()
            }
        }

        view = findViewById(R.id.image_view_image_detail)
        view.setImageResource(R.color.pixiv_blue)
        var width = 0
        var height = 0
        view.post {
            width = view.width
            height = (width / ratio).toInt()
            view.layoutParams.height = height
            Log.e("Width", width.toString())
            view.setImageResource(R.color.pixiv_blue)
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
            handler.send(UIHandler.RUN_ANY, UIDetail(runnable = {
//                image_button_image_detail_user_profile.setImageURI(profileUri.toUri())
                Glide.with(applicationContext).load(profileUri).into(
                    image_button_image_detail_user_profile
                )
            }))
        }.start()

        Thread {
            setImage(
                view,
                illustId,
                originalUri,
                ratio,
                progress_bar_image_detail.id,
                thumbUrl
            )
        }.start()
        Thread {
            setInfo(illustId)
        }.start()


        view.onSingleClick({
            val inflater = LayoutInflater.from(this)
            val imgEntryView: View =
                inflater.inflate(R.layout.dialog_photo_entry, null) // 加载自定义的布局文件
            val dialog = ImageDialog(this, R.style.FullScreenDialog)
            dialog.setContentView(imgEntryView)
            dialog.window!!.setBackgroundDrawableResource(R.color.black)
            dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            dialog.window!!.decorView.setPadding(0, 0, 0, 0)
            dialog.window!!.decorView.background = R.color.black.toDrawable()
            dialog.window!!.setLayout(screenSize[0], screenSize[1])
            Log.e("url",originalUri)
            println("screenSize$screenSize")
            println("density${DisplayMetrics().density}")
//            val htmlHeight=(screenSize[0]/picWidth*picHeight).toInt().px2dp().toInt()
//            println(htmlHeight)

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
            				<img  src="file:///$originalUri" style="width:100%;">
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
            handler.post{
//                handler.setImage(img, originalUri, ratio)
                img.settings.setSupportZoom(true)
                img.settings.useWideViewPort = true
                img.settings.builtInZoomControls = true
                img.settings.displayZoomControls = false
                img.settings.javaScriptEnabled = true
                img.setInitialScale(25)
                img.loadDataWithBaseURL(null, data,"text/html","UTF-8", null)
            }
            img.setOnClickListener {
                dialog.cancel()
            }
            imgEntryView.setOnClickListener{
                dialog.cancel()
            }
        })


    }
}