package com.hardrelice.pixivzh.ui.main.frg

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hardrelice.pixivzh.R
import com.hardrelice.pixivzh.mvp.view.BaseFragment
import com.hardrelice.pixivzh.ui.main.datatype.RankItem
import com.hardrelice.pixivzh.ui.main.adapter.HomePageAdapter
import com.hardrelice.pixivzh.utils.handler
import kotlinx.android.synthetic.main.card_list.view.*
import kotlinx.android.synthetic.main.fragment_rank.view.*

class HomeFragment: BaseFragment() {
    private lateinit var viewModel: HomeViewModel
    override fun setData() {

    }

//    fun getMainPageList(num:Int):List<RankItem>{
//        val list = ArrayList<RankItem>()
//        for (i in 0 until num){
//            val drawable = when (i % 3) {
//                0 -> R.drawable.icon_home_active
//                1 -> R.drawable.icon_saved_active
//                else -> R.drawable.icon_rank_active
//            }
//            val item = RankItem(drawable, "Rank $i", "Detail $i ")
//            list += item
//        }
//        return list
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        root.card_list_recycler_view.adapter= HomePageAdapter(getMainPageList(10))
//        root.card_list_recycler_view.layoutManager = LinearLayoutManager(this.activity)
//        root.card_list_recycler_view.setHasFixedSize(true)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })


        return root
    }

}