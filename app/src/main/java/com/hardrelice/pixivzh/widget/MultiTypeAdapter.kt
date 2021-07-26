package com.hardrelice.pixivzh.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Method
import javax.security.auth.callback.Callback


open class MultiTypeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val arrayList = mutableListOf<Any>()
    val holderMap = hashMapOf<Int, Class<out RecyclerView.ViewHolder?>>()

    fun setItems(items: ArrayList<Any>) {
        arrayList.clear()
        arrayList.addAll(items)
        println(arrayList[10])
        notifyDataSetChanged()
    }

    fun addItem(position: Int, item: Any) {
        arrayList.add(position, item)
        notifyItemInserted(position)
    }

    fun addItem(item: Any){
        arrayList.add(item)
        notifyItemInserted(arrayList.size - 1)
    }

    fun addItems(position: Int, items: Collection<Any>){
        arrayList.addAll(position, items)
        notifyItemRangeChanged(position,items.size)
    }

    fun addItems(items: Collection<Any>){
        arrayList.addAll(items)
        notifyItemRangeChanged(arrayList.size-items.size, items.size)
    }

    fun removeItem(position: Int) {
        arrayList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItems(position: Int, itemCount: Int){
        for (i in position until position + itemCount) arrayList.removeAt(position)
        notifyItemRangeRemoved(position, itemCount)
    }

    fun replaceItem(position: Int, item: Any) {
        arrayList.removeAt(position)
        arrayList.add(position, item)
        notifyItemChanged(position)
    }

    fun registerType(
        javaClass: Class<*>,
        viewType: Class<out RecyclerView.ViewHolder?>
    ): Boolean {
        if (javaClass.hashCode() in holderMap.keys) return false
        holderMap[javaClass.hashCode()] = viewType
        return true
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView
    ) {
        abstract fun setData(position: Int, data: Any)
        fun getItemView() = itemView
    }

    override fun getItemViewType(position: Int): Int {
        return arrayList[position].javaClass.hashCode()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return holderMap[viewType]!!
            .getConstructor(ViewGroup::class.java)
            .newInstance(parent)!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val setData: Method = holder.javaClass
            .getDeclaredMethod(
                "setData",
                Int::class.java,
                Any::class.java
            )
        setData(holder, position, arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}