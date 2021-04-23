package com.hardrelice.pixivzh.ui.main.frg

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hardrelice.pixiver.UIDetail
import com.hardrelice.pixivzh.utils.UIHandler
import com.hardrelice.pixivzh.Pixiv
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.mvp.view.BaseFragment
import com.hardrelice.pixivzh.ui.main.adapter.SearchAdapter
import com.hardrelice.pixivzh.ui.main.datatype.SearchItem
import com.hardrelice.pixivzh.ui.main.datatype.SearchSetting
import com.hardrelice.pixivzh.utils.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.search_view_search_bar
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : BaseFragment() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var root: View
    private var saved: Boolean = false
    private var search: MutableList<SearchItem> = mutableListOf()
    private var content: String? = null
    private var loading = false
    private var loadingMore = false
    private var setting = SearchSetting()

    val nav_home_view = ApplicationUtil.Activity!!.findViewById<View>(R.id.nav_home)

    override fun setData() {
        if (!saved) {
            saved = true
            handler.send(
                UIHandler.SET_FRAGMENT,
                UIDetail(
                    view = search_recycler_view,
                    obj = search,
                    activity = this.requireActivity()
                )
            )
        }
    }

    fun getWord(): String {
        return root.search_view_search_bar.query.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_search, container, false)
        root.search_recycler_view.adapter = SearchAdapter(search, this.requireActivity())
        root.search_recycler_view.layoutManager = GridLayoutManager(this.activity, 4)
        root.search_recycler_view.setHasFixedSize(false)
        root.search_recycler_view.setListener(object : RecyclerListener {
            override fun loadMore() {
                if (!loading) {
                    loading = true
                    root.progress_bar_search_loadmore.visibility = View.VISIBLE
                    Thread {
                        val data = Pixiv.search(getWord(), setting)
                        if (data.size != 0) {
                            println("LOADMORE")
                            handler.send(
                                UIHandler.UPDATE_FRAGMENT,
                                UIDetail(
                                    view = root.search_recycler_view,
                                    obj = data,
                                    int = search.size
                                )
                            )
                            setting.p++
                        }
                        loading = false
                        handler.post {
                            root.progress_bar_search_loadmore.visibility = View.GONE
                        }
                    }.start()
                }
            }
            override fun refresh() {
            }
        })

        root.search_swipe_refresh_layout.setOnRefreshListener {
            Thread {
                setting.p = 1
                try {
                    search = mutableListOf()
                    search.addAll(Pixiv.search(getWord(), setting))
                    setting.p++
                } catch (e: Exception) {
                    Toast.makeText(
                        ApplicationUtil.ApplicationContext,
                        "Request timeout! ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (search.size != 0) {
                    handler.send(
                        UIHandler.SET_FRAGMENT,
                        UIDetail(
                            view = search_recycler_view,
                            obj = search,
                            activity = this.requireActivity()
                        )
                    )
                } else {
                    Toast.makeText(
                        ApplicationUtil.ApplicationContext,
                        "SetData Exception!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("setData", "wrong")
                }
                handler.post {
                    root.search_swipe_refresh_layout.isRefreshing = false
                }
            }.start()
        }

        root.search_swipe_refresh_layout.setColorSchemeColors(R.color.pixiv_blue.getColor())

        root.search_view_search_bar.isSubmitButtonEnabled = true
        root.search_view_search_bar.isIconified = false
        root.search_view_search_bar.setIconifiedByDefault(false)

        root.search_view_search_bar.setOnQueryTextFocusChangeListener { v, hasFocus ->
            when {
                hasFocus -> {

                }
                else -> {
                    handler.post {
                        ApplicationUtil.Activity!!.currentFocus?.clearFocus()
                        val imm =
                            activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(search_view_search_bar.windowToken, 0)
                    }
                }
            }
        }

        return root
    }
}