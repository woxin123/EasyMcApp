package top.mcwebsite.novel.ui.rank.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import top.mcwebsite.common.ui.base.BaseFragment
import top.mcwebsite.novel.databinding.FragmentRankNovelItemBinding
import top.mcwebsite.novel.model.RankCategory
import top.mcwebsite.novel.ui.rank.BookRankAdapter
import top.mcwebsite.novel.ui.rank.RankCategoryAdapter
import top.mcwebsite.novel.ui.rank.RankFragmentDirections
import top.mcwebsite.novel.ui.rank.RankViewModel

class ABCNovelRankFragment(private val source: String) :
    BaseFragment<FragmentRankNovelItemBinding>() {

    private val viewModel: RankViewModel by inject()

    private lateinit var bookRankAdapter: BookRankAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRankNovelItemBinding {
        return FragmentRankNovelItemBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initData()
        initObservable()
    }

    private fun initView() {

    }

    private fun initData() {
        viewModel.requireRankCategoriesBySource(source)
    }

    private fun initRankList(category: List<RankCategory>) {
        binding.rankList.apply {
            adapter = RankCategoryAdapter(category.map { it.name }) {
                bookRankAdapter.refreshData(category[it].bookModels)
            }
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
        binding.rankItemList.apply {
            bookRankAdapter = BookRankAdapter(category[0].bookModels) {
                findNavController().navigate(
                    RankFragmentDirections.actionRankFragmentToBookDetailFragment(it)
                )
            }
            adapter = bookRankAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun initObservable() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rankCategoriesEvent.collect {
                    initRankList(it)
                }
            }
        }
    }

}