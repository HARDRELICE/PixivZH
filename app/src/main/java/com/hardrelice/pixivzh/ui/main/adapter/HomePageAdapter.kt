package com.hardrelice.pixivzh.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import kotlinx.android.synthetic.main.card_top.view.*

class HomePageAdapter(private val itemList: List<RankItem>) :
    RecyclerView.Adapter<HomePageAdapter.HomePageViewHolder>() {


    class HomePageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view_top
        val textViewRank: TextView = itemView.text_view_rank
        val textViewDetail: TextView = itemView.text_view_user_info
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)
        return HomePageViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomePageViewHolder, position: Int) {
        val currentItem = itemList[position]

//        holder.imageView.setImageResource(currentItem.)
//        holder.textViewRank.text = currentItem.rank
//        holder.textViewDetail.text = currentItem.detail
    }

}