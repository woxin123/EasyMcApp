package top.mcwebsite.novel.ui.bookshelf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.databinding.FragmentBookShelfBinding
import java.lang.IllegalStateException

class BookShelfFragment : Fragment() {

    private lateinit var fragmentBookShelfBinding: FragmentBookShelfBinding

    private val viewModel: BookshelfViewModel by viewModel()

    private lateinit var adapter: BookShelfRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBookShelfBinding = FragmentBookShelfBinding.inflate(inflater, container, false)
        return fragmentBookShelfBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservable()
    }

    private fun initView() {
        fragmentBookShelfBinding.recyclerview.let { recyclerView ->
            adapter = BookShelfRecyclerAdapter(viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }
    }

    private fun initObservable() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookshelfEvent.collect {
                        adapter.setBooks(it)
                    }
                }
                launch {
                    viewModel.clickItemEvent.collect {
                        val action =
                            BookShelfFragmentDirections.actionBookShelfFragmentToReadBookFragment(
                                it
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

}