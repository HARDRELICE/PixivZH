package com.hardrelice.pixivzh.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixiver.UIHandler
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.Requests
import com.hardrelice.pixivzh.ui.main.act.ImageDetailActivity
import com.hardrelice.pixivzh.ui.main.datatype.SearchItem
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.card_search.view.*
import java.io.File

class SearchAdapter(itemList: List<SearchItem>, activity: FragmentActivity) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    lateinit var context: Context
    var handler = UIHandler(activity)
    var mutableItemList = itemList as MutableList<SearchItem>
    var taskList = hashMapOf<String, Boolean>()
    var lastTime: Long = System.currentTimeMillis()

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view_search
        val textViewSearchTitle: TextView = itemView.text_view_search_title
        val textViewUserName: TextView = itemView.text_view_user_name_search
        val cardView: CardView = itemView.card_view_card_search
    }

    fun asyncLoadImage(
        illustId: String,
        thumbUrl: String,
        view: View,
        position: Int,
        holder: SearchViewHolder,
        ratio: Float
    ) {
        Log.e(illustId, view.id.toString())
        Log.e("holder=======>", "${holder.layoutPosition} ${holder.adapterPosition} $position")
        val baseUri = FileHandler.getIllustFolder(illustId)
        val uri = FileHandler.getIllustFolder(illustId, "thumb.jpg")
        FileHandler.checkDir(baseUri)
        println("$illustId ${taskList[illustId]}")
        try {
            if (!File(uri).exists()) {
                taskList[illustId] = false
                Requests.download(thumbUrl.replace("i.pximg.net", pximg_host), pixiv_headers, uri)
                taskList[illustId] = true
            }
            if (holder.layoutPosition == position) {
                if (taskList[illustId] == null || taskList[illustId] == true) {
                    val msg = Message()
                    msg.what = UIHandler.SET_IMAGE_VIEW
                    val detail = UIDetail(view = view, string = uri, float = ratio)
                    msg.obj = detail
                    handler.sendMessage(msg)
                }
            }
        } catch (e: Exception) {
            Log.e("asyncLoadImage", e.message!!)
        }
    }

    fun addItem(position: Int, SearchItem: SearchItem) {
        mutableItemList.add(position, SearchItem)
    }

    fun addRangeItem(position: Int, itemCount: Int, SearchItems: List<SearchItem>) {
        mutableItemList.addAll(position, SearchItems)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mutableItemList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        Log.e("onBindViewHolder", "$position")

        val currentItem = mutableItemList[position]

        val view = holder.imageView

        val ratio: Float = currentItem.width.toFloat() / currentItem.height.toFloat()
        Log.e("ratio=======>", "$ratio")
        Log.e("width=======>", "${view.width}")

        var height = (view.width / ratio).toInt()
//        if(height == 0) {
//            val padding = holder.cardView.paddingLeft+holder.cardView.paddingRight+holder.imageView.paddingLeft+holder.imageView.paddingRight
//            view.layoutParams.width = screenSize[0] - padding.dp2px()
//            height = (view.layoutParams.width/ratio).toInt()
//            println(view.layoutParams.width)
//            println(view.layoutParams.height)
//        }

        Log.e("height=======>", "${height}")


        if (height==0){
//            view.setImageResource(R.drawable.image_placeholder_loading_blank)
            view.layoutParams.height = height
            view.setImageResource(R.drawable.image_placeholder_loading_blank)
        } else{
            view.layoutParams.height = height
            view.setImageResource(R.color.pixiv_blue)
        }

        Thread {
            asyncLoadImage(
                currentItem.illustId,
                currentItem.thumbUrl,
                holder.imageView,
                position,
                holder,
                ratio
            )
        }.start()
        holder.textViewUserName.text = currentItem.userName
        holder.textViewSearchTitle.text = currentItem.title
        holder.imageView.setOnClickListener {
            if (System.currentTimeMillis() - lastTime > 1000) {
                lastTime = System.currentTimeMillis()
                context = ApplicationUtil.ApplicationContext!!
                val uri = FileHandler.getIllustFolder(currentItem.illustId, "thumb.jpg")
                val mIntent = Intent(context, ImageDetailActivity::class.java)
                mIntent.putExtra("page_count", currentItem.pageCount)
                mIntent.putExtra("position", position)
                mIntent.putExtra("user_id", currentItem.userId)
                mIntent.putExtra("user_name", currentItem.userName)
                mIntent.putExtra("illust_id", currentItem.illustId)
                mIntent.putExtra("title", currentItem.title)
                mIntent.putExtra("ratio", ratio)
                mIntent.putExtra("uri", uri)
                mIntent.putExtra("thumb_url", currentItem.thumbUrl)
                mIntent.putExtra("profile_img", currentItem.profileImg)
                mIntent.putStringArrayListExtra("tags", currentItem.tags)
                println(currentItem.thumbUrl)
                context.startActivity(mIntent)
            }
        }
    }
}