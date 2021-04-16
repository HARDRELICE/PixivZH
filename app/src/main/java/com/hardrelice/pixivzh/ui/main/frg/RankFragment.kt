package com.hardrelice.pixivzh.ui.main.frg

import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixiver.UIHandler
import com.hardrelice.pixivzh.Pixiv
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.mvp.view.BaseFragment
import com.hardrelice.pixivzh.ui.main.adapter.RankAdapter
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.fragment_rank.*
import kotlinx.android.synthetic.main.fragment_rank.view.*
import java.util.*

class RankFragment : BaseFragment() {
    private lateinit var viewModel: RankViewModel
    private lateinit var root: View
    private var saved: Boolean = false
    private var rank: MutableList<RankItem> = mutableListOf()
    private var date: String? = null
    private var mode = "daily"
    private var content: String? = null
    private var p = 1
    private var loading = false
    private var loadingMore = false

    override fun setData() {
        if (!saved) {
            saved = true
            p = 1
//            progress_bar_rank_refresh.visibility = View.VISIBLE
            root.rank_swipe_refresh_layout.isRefreshing = true
            root.tab_rank_type.getTabAt(0)?.view?.isEnabled = false
            root.tab_rank_type.getTabAt(1)?.view?.isEnabled = false
            root.tab_rank_type.getTabAt(2)?.view?.isEnabled = false
            loading = true
            Thread {
                try {
                    rank = mutableListOf()
                    rank.addAll(Pixiv.getRank(date, mode, content, p))
                    p++
                } catch (e: Exception) {
                    saved = false
                }
                if (rank.size != 0) {
                    val msg = Message()
                    msg.what = UIHandler.SET_FRAGMENT
                    val detail = UIDetail(
                        view = rank_recycler_view,
                        obj = rank,
                        activity = this.requireActivity()
                    )
                    msg.obj = detail
                    handler.sendMessage(msg)
                } else {
                    saved = false
//                    val button: Button = Button(context)
//                    this.view?.addTouchables(arrayListOf<View>(button))
                    Log.e("setData", "wrong")
                }
                val runnable = Runnable {
//                    progress_bar_rank_refresh.visibility = View.GONE
                    root.rank_swipe_refresh_layout.isRefreshing = false
                    root.tab_rank_type.getTabAt(0)?.view?.isEnabled = true
                    root.tab_rank_type.getTabAt(1)?.view?.isEnabled = true
                    root.tab_rank_type.getTabAt(2)?.view?.isEnabled = true
                }
                handler.send(
                    UIHandler.RUN_ANY,
                    UIDetail(
                        runnable = runnable
                    )
                )
                loading = false
            }.start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(RankViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_rank, container, false)
        root.rank_recycler_view.adapter = RankAdapter(rank, this.requireActivity())
        root.rank_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        root.rank_recycler_view.setHasFixedSize(false)
        root.rank_recycler_view.setListener(object : RecyclerListener {
            override fun loadMore() {
                root.tab_rank_type.getTabAt(0)?.view?.isEnabled = false
                root.tab_rank_type.getTabAt(1)?.view?.isEnabled = false
                root.tab_rank_type.getTabAt(2)?.view?.isEnabled = false
                if (!loading) {
                    if(p>=11){
                        if(p==11) {
                            handler.toast("No More Pics Underneath...")
                            p++
                        }
                        return
                    }
                    loading = true
                    root.progress_bar_rank_loadmore.visibility = View.VISIBLE
                    Thread {
                        val data = Pixiv.getRank(date, mode, content, p)
                        if (data.size != 0) {
                            println("LOADMORE")
//                            var positionStart = rank.size
//                            rank.addAll(data)
//                            (root.rank_recycler_view.adapter as RankAdapter).notifyItemRangeInserted(positionStart, data.size)
                            val detail = UIDetail(
                                view = root.rank_recycler_view,
                                obj = data,
                                int = rank.size
                            )
                            handler.send(
                                UIHandler.UPDATE_FRAGMENT,
                                detail
                            )
                            p++
                        }
                        loading = false
                        val runnable = Runnable {
                            root.progress_bar_rank_loadmore.visibility = View.GONE
                            root.tab_rank_type.getTabAt(0)?.view?.isEnabled = true
                            root.tab_rank_type.getTabAt(1)?.view?.isEnabled = true
                            root.tab_rank_type.getTabAt(2)?.view?.isEnabled = true
                        }
                        handler.send(
                            UIHandler.RUN_ANY,
                            UIDetail(
                                runnable = runnable
                            )
                        )
                    }.start()
                }
            }

            override fun refresh() {
                saved = false
                setData()
            }
        })

        root.rank_swipe_refresh_layout.setOnRefreshListener {
            Thread {
                handler.post{
                    root.tab_rank_type.getTabAt(0)?.view?.isEnabled = false
                    root.tab_rank_type.getTabAt(1)?.view?.isEnabled = false
                    root.tab_rank_type.getTabAt(2)?.view?.isEnabled = false
                }
                p = 1
                try {
                    rank = mutableListOf()
                    rank.addAll(Pixiv.getRank(date, mode, content, p))
                    p++
                } catch (e: Exception) {
                    handler.toast("Set Data Failed")
                }
                if (rank.size != 0) {
                    val msg = Message()
                    msg.what = UIHandler.SET_FRAGMENT
                    val detail = UIDetail(
                        view = rank_recycler_view,
                        obj = rank,
                        activity = this.requireActivity()
                    )
                    msg.obj = detail
                    handler.sendMessage(msg)
                } else {
//                    handler.toast("Set Data Failed")
                    Log.e("setData", "wrong")
                }
                handler.post{
                    root.rank_swipe_refresh_layout.isRefreshing = false
                    root.tab_rank_type.getTabAt(0)?.view?.isEnabled = true
                    root.tab_rank_type.getTabAt(1)?.view?.isEnabled = true
                    root.tab_rank_type.getTabAt(2)?.view?.isEnabled = true
                }
            }.start()
        }

        root.rank_swipe_refresh_layout.setColorSchemeColors(R.color.pixiv_blue.getColor())

        println(root)
        println(root.rank_recycler_view)
        println(rank_recycler_view)
        println(root.tab_rank_type)
        println(root.tab_rank_type.get(0))
        println(tab_item_rank_daily)

        root.tab_rank_type.getTabAt(0)?.view?.setOnClickListener {
            if (mode != "daily") {
                mode = "daily"
                saved = false
                setData()
            } else {
                if (!loading) {
                    mode = "daily"
                    saved = false
                    setData()
                }
            }
        }
        root.tab_rank_type.getTabAt(1)?.view?.setOnClickListener {
            if (mode != "weekly") {
                mode = "weekly"
                saved = false
                setData()
            } else {
                if (!loading) {
                    mode = "weekly"
                    saved = false
                    setData()
                }
            }
        }
        root.tab_rank_type.getTabAt(2)?.view?.setOnClickListener {
            if (mode != "monthly") {
                mode = "monthly"
                saved = false
                setData()
            } else {
                if (!loading) {
                    mode = "monthly"
                    saved = false
                    setData()
                }
            }
        }

        return root
    }
}