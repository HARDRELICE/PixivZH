package com.hardrelice.pixivzh.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

interface RecyclerListener {
    fun loadMore()
    fun refresh()

}

fun RecyclerView.setListener(l: RecyclerListener){
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var lastVisibleItem: Int = 0
        val swipeRefreshLayout = this@setListener.parent
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager:RecyclerView.LayoutManager = recyclerView.layoutManager!!

            if (layoutManager is LinearLayoutManager) {
                lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            } else if (layoutManager is GridLayoutManager) {
                lastVisibleItem = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            } else if(layoutManager is StaggeredGridLayoutManager) {
                val staggeredGridLayoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions)
                lastVisibleItem = findMax(lastPositions)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.adapter?.itemCount) {
                //下拉刷新的时候不可以加载更多
                if(swipeRefreshLayout is SwipeRefreshLayout){
                    if(!swipeRefreshLayout.isRefreshing){
                        l.loadMore()
                    }
                }else{
                    l.loadMore()
                }
            }
        }
    })

    val swipeRefreshLayout = this.parent
    if(swipeRefreshLayout is SwipeRefreshLayout){
        swipeRefreshLayout.setOnRefreshListener {
            l.refresh()
        }
    }

}
/**
 * 取数组中最大值
 *
 * @param lastPositions
 * @return
 */
fun  findMax(lastPositions:IntArray):Int {
    var max = lastPositions[0]
    for ( i in lastPositions.indices) {
        val value = lastPositions[i]
        if (value > max) {
            max = value
        }
    }

    return max;
}
