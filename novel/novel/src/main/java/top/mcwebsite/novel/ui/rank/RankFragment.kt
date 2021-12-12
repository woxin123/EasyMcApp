package top.mcwebsite.novel.ui.rank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import top.mcwebsite.common.ui.base.BaseFragment
import top.mcwebsite.novel.data.remote.repository.impl.Yb3BookRepository
import top.mcwebsite.novel.databinding.FragmentRankBinding
import top.mcwebsite.novel.ui.rank.item.ABCNovelRankFragment


class RankFragment : BaseFragment<FragmentRankBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRankBinding {
        return FragmentRankBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val title = mutableListOf("ABC")
        val rankItemFragments = mutableListOf(ABCNovelRankFragment(Yb3BookRepository.source))
        binding.viewPager.adapter = RankViewPagerAdapter(this, rankItemFragments)
        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = title[position]
        }
        tabLayoutMediator.attach()
    }


}