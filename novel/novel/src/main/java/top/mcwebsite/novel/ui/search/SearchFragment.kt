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
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel()

    private val imm by lazy {
        ContextCompat.getSystemService(this.requireContext(), InputMethodManager::class.java)
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
        binding.searchContent.requestFocus()
        imm?.showSoftInput(binding.searchContent, 0)
        viewModel.searchContent.observe(this.viewLifecycleOwner) {
            Log.d("mengchen", "search content = $it")
        }

        viewModel.backEvent.observe(this.viewLifecycleOwner) {
            binding.searchContent.let {
                it.clearFocus()
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
                findNavController().popBackStack()
            }
        }

        binding.recentSearchRecycle.let { recentSearchRecycle ->
            val adapter = RecentSearchRecyclerAdapter(viewModel)
            recentSearchRecycle.adapter = adapter
            recentSearchRecycle.layoutManager = LinearLayoutManager(this@SearchFragment.requireContext())
            viewModel.searchHistoryData.observe(this@SearchFragment.viewLifecycleOwner) {
                adapter.setData(it)
            }
        }


    }
}