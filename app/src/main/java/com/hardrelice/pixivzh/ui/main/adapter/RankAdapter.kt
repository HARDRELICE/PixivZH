package com.hardrelice.pixivzh.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixivzh.utils.UIHandler
import com.hardrelice.pixivzh.FileHandler
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.utils.Requests
import com.hardrelice.pixivzh.ui.main.act.ImageDetailActivity
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.card_top.view.*
import java.io.BufferedOutputStream
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class RankAdapter(itemList: List<RankItem>, activity: FragmentActivity) :
    RecyclerView.Adapter<RankAdapter.RankViewHolder>() {
    lateinit var context: Context
    var handler = UIHandler(activity)
    var mutableItemList = itemList as MutableList<RankItem>
//    var taskList = hashMapOf<String, Boolean>()

    class RankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view_top
        val textViewRank: TextView = itemView.text_view_rank
        val textViewRankTitle: TextView = itemView.text_view_rank_title
        val textViewUserInfo: TextView = itemView.text_view_user_info
        val textViewIllustId: TextView = itemView.text_view_illust_id
        val textViewUserName: TextView = itemView.text_view_user_name
        val cardView: CardView = itemView.card_view_card_top
    }

    fun asyncLoadImage(
        illustId: String,
        thumbUrl: String,
        view: ImageView,
        position: Int,
        holder: RankViewHolder,
    ) {
        Log.e(illustId, view.id.toString())
        Log.e("holder=======>", "${holder.layoutPosition} ${holder.adapterPosition} $position")
        val baseUri = FileHandler.getIllustFolder(illustId)
        val uri = FileHandler.getIllustFolder(illustId, "thumb.jpg")
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

    fun addItem(position: Int, rankItem: RankItem) {
        mutableItemList.add(position, rankItem)
    }

    fun addRangeItem(position: Int, itemCount: Int, rankItems: List<RankItem>) {
        mutableItemList.addAll(position, rankItems)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        return RankViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mutableItemList.size
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        Log.e("onBindViewHolder", "$position")

        val currentItem = mutableItemList[position]
        val view = holder.imageView
        val lp = view.layoutParams
        val density =
            ApplicationUtil.ApplicationContext!!.resources.displayMetrics.densityDpi / 160.toFloat()
        lp.width = (screenSize[0].toFloat() - 40.dp2px()).toInt()
//        lp.width = view.layoutParams.width
        lp.height =
            (lp.width.toFloat() / currentItem.width.toFloat() * currentItem.height.toFloat()).toInt()
        view.layoutParams = lp
//        view.layoutParams.width = lp.width
//        view.layoutParams.height = lp.height

        view.setImageResource(R.color.pixiv_blue)

        Log.e("width=======>", "${view.layoutParams.width}")
        Thread {
            asyncLoadImage(
                currentItem.illustId,
                currentItem.thumbUrl,
                holder.imageView,
                position,
                holder,
            )
        }.start()
        holder.textViewRank.text = currentItem.rank
        holder.textViewUserInfo.text = currentItem.userId
        holder.textViewUserName.text = currentItem.userName
        holder.textViewIllustId.text = currentItem.illustId
        holder.textViewRankTitle.text = currentItem.title
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