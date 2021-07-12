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
import com.hardrelice.pixivzh.utils.UIHandler
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.Requests
import com.hardrelice.pixivzh.ui.main.act.ImageDetailActivity
import com.hardrelice.pixivzh.ui.main.datatype.SearchItem
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.card_search.view.*
import java.io.BufferedOutputStream
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

    private fun asyncLoadImage(
        illustId: String,
        thumbUrl: String,
        view: ImageView,
        position: Int,
        holder: SearchViewHolder,
    ) {
        val baseUri = FileHandler.getIllustFolder(illustId)
        val uri = FileHandler.getIllustFolder(illustId, "thumb_square.jpg")
        val tempUri = FileHandler.getIllustFolder(illustId, System.currentTimeMillis().toString()+"thumb_temp.jpg")
        FileHandler.checkDir(baseUri)
        try {
            if (!File(uri).exists()) {
                Requests.download(
                    thumbUrl.replace("i.pximg.net", pximg_host),
                    pixiv_headers,
                    tempUri
                )
                File(tempUri).inputStream().use { input ->
                    BufferedOutputStream(File(uri).outputStream()).use { output ->
                        input.copyTo(output)
                    }
                }
                File(tempUri).delete()
            }
            if (holder.layoutPosition == position) {
                if (File(uri).exists()) {
                    val msg = Message()
                    msg.what = UIHandler.SET_IMAGE_VIEW
                    val detail = UIDetail(view = view, string = uri)
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
            LayoutInflater.from(parent.context).inflate(R.layout.card_search, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mutableItemList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
//        Log.e("onBindViewHolder", "$position")

        val currentItem = mutableItemList[position]

        val view = holder.imageView

        val trueratio: Float = currentItem.width.toFloat() / currentItem.height.toFloat()
        val ratio: Float = 1f
//        Log.e("ratio=======>", "$ratio")
//        Log.e("width=======>", "${view.width}")

        val height = (view.width / ratio).toInt()

//        Log.e("height=======>", "${height}")


        if (height==0){
//            view.setImageResource(R.drawable.image_placeholder_loading_blank)
            view.layoutParams.height = height
            view.setImageResource(R.drawable.image_placeholder_loading_square)
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
                holder
            )
        }.start()
        holder.textViewUserName.text = currentItem.userName
        holder.textViewSearchTitle.text = currentItem.title
        holder.imageView.onSingleClick({
            context = ApplicationUtil.ApplicationContext!!
            val uri = FileHandler.getIllustFolder(currentItem.illustId, "thumb.jpg")
            val mIntent = Intent(context, ImageDetailActivity::class.java)
            mIntent.putExtra("page_count", currentItem.pageCount)
            mIntent.putExtra("position", position)
            mIntent.putExtra("user_id", currentItem.userId)
            mIntent.putExtra("user_name", currentItem.userName)
            mIntent.putExtra("illust_id", currentItem.illustId)
            mIntent.putExtra("title", currentItem.title)
            mIntent.putExtra("width", currentItem.width.toFloat())
            mIntent.putExtra("height", currentItem.height.toFloat())
            mIntent.putExtra("uri", uri)
            mIntent.putExtra("thumb_url", currentItem.thumbUrl)
            mIntent.putExtra("profile_img", currentItem.profileImg)
            mIntent.putStringArrayListExtra("tags", currentItem.tags)
            println(currentItem.thumbUrl)
            context.startActivity(mIntent)
        })
    }
}