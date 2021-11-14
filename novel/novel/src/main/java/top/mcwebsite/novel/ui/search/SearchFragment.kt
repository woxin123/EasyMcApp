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

    private lateinit var searchResultRecyclerAdapter: SearchResultRecyclerAdapter

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
            recentSearchRecycle.layoutManager =
                LinearLayoutManager(this@SearchFragment.requireContext())
            viewModel.searchHistoryData.observe(this@SearchFragment.viewLifecycleOwner) {
                adapter.setData(it)
            }
        }
        binding.searchResultRecycle.let { searchResultRecycler ->
            searchResultRecyclerAdapter = SearchResultRecyclerAdapter(viewModel)
            searchResultRecycler.adapter = searchResultRecyclerAdapter
            searchResultRecycler.layoutManager =
                LinearLayoutManager(this@SearchFragment.requireContext())


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
                findNavController().navigateUp()
            }
        }
        viewModel.searchResult.observe(this.viewLifecycleOwner) {
            loadingDialog.dismiss()
            showOrHideRecent(false)
            binding.sourceErrorView.root.visibility = View.GONE
            binding.searchResultRecycle.visibility = View.VISIBLE
            searchResultRecyclerAdapter.setData(it)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                            SearchFragmentDirections.actionSearchFragmentToBookDetailFragment(it)
                        )
                    }
                }
                launch {
                    viewModel.searchItemUpdateEvent.collect {
                        searchResultRecyclerAdapter.updateBookItem(it.first, it.second)
                    }
                }
                launch {
                    viewModel.searchFailedEvent.collect { bookSourceException ->
                        loadingDialog.dismiss()
                        binding.apply {
                            showOrHideRecent(false)
                            sourceErrorView.apply {
                                root.visibility = View.VISIBLE
                                errorMessage.text = resources.getString(R.string.book_source_error, bookSourceException.bookSource)
                                removeAndRetry.text = resources.getString(R.string.remove_book_source_and_retry, bookSourceException.bookSource)
                                sourceErrorView.apply {
                                    fun retry() {
                                        this@SearchFragment.viewModel.search()
                                    }
                                    removeAndRetry.setOnClickListener {
                                        this@SearchFragment.viewModel.removeSearchBookRepository(bookSourceException.bookSource)
                                        retry()
                                    }
                                    retry.setOnClickListener {
                                        retry()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}