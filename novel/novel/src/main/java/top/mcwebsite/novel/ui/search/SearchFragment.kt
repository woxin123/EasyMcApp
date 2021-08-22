package top.mcwebsite.novel.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.common.ui.loading.LoadingDialog
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel()

    private val imm by lazy {
        ContextCompat.getSystemService(this.requireContext(), InputMethodManager::class.java)
    }

    private val loadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false).also {
            binding = FragmentSearchBinding.bind(it)
            binding.lifecycleOwner = this.viewLifecycleOwner
            binding.viewModel = viewModel
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservable()
    }

    private fun initView() {
        binding.searchContent.requestFocus()
//        imm?.showSoftInput(binding.searchContent, 0)
        binding.recentSearchRecycle.let { recentSearchRecycle ->
            val adapter = RecentSearchRecyclerAdapter(viewModel)
            recentSearchRecycle.adapter = adapter
            recentSearchRecycle.layoutManager = LinearLayoutManager(this@SearchFragment.requireContext())
            viewModel.searchHistoryData.observe(this@SearchFragment.viewLifecycleOwner) {
                adapter.setData(it)
            }
        }
        binding.searchResultRecycle.let { searchResultRecycler ->
            val adapter = SearchResultRecyclerAdapter(viewModel)
            searchResultRecycler.adapter = adapter
            searchResultRecycler.layoutManager = LinearLayoutManager(this@SearchFragment.requireContext())
            viewModel.searchResult.observe(this.viewLifecycleOwner) {
                loadingDialog.dismiss()
                showOrHideRecent(false)
                binding.searchResultRecycle.visibility = View.VISIBLE
                adapter.setData(it)
            }
        }
    }

    private fun showOrHideRecent(show: Boolean) {
        val visible = if (show) View.VISIBLE else View.GONE
        binding.recentSearchRecycle.visibility = visible
        binding.recentSearchText.visibility = visible
        binding.clearAll.visibility = visible
    }

    private fun initObservable() {
        viewModel.backEvent.observe(this.viewLifecycleOwner) {
            binding.searchContent.let {
                it.clearFocus()
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
                findNavController().popBackStack()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchEvent.collect {
                        loadingDialog.showLoading("Searching...", delayShowClose = 2000) {
                            viewModel.cancelSearch()
                        }
                    }
                }
                launch {
                    viewModel.clickSearchResultEvent.collect {
                        findNavController().navigate(
                            R.id.action_searchFragment_to_bookDetailFragment,
                            Bundle().apply {
                                putParcelable(Constant.BOOK_MODEL, it)
                            })
                    }
                }
            }

        }

    }
}