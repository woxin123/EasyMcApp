package top.mcwebsite.novel.ui.bookshelf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.common.android.ext.setVisible
import top.mcwebsite.common.android.ext.showShortToast
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentBookShelfBinding
import top.mcwebsite.novel.ui.home.MainViewModel

class BookShelfFragment : Fragment() {

    private lateinit var binding: FragmentBookShelfBinding

    private val viewModel: BookshelfViewModel by viewModel()

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: BookShelfRecyclerAdapter

    private lateinit var editStatusBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBookShelfBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObservable()
    }

    private fun initView() {
        binding.recyclerview.let { recyclerView ->
            adapter = BookShelfRecyclerAdapter(viewModel)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            recyclerView.itemAnimator?.changeDuration = 0
        }
        editStatusBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                adapter.closeEdit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, editStatusBackPressedCallback)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefreshBooks()
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

                launch {
                    viewModel.checkBookNumFlow.collect {
                        if (it == 0) {
                            binding.deleteBtn.text = resources.getString(R.string.delete)
                            binding.deleteBtn.alpha = 0.4F
                        } else {
                            binding.deleteBtn.text = resources.getString(R.string.delete_with_num, it)
                            binding.deleteBtn.alpha = 1F
                        }
                    }
                }

                launch {
                    viewModel.editStatus.collect { status ->
                        editStatusBackPressedCallback.isEnabled = status
                        mainViewModel.changeBottomNavigationStatus(!status)
                        binding.editViews.setVisible(status)
                        binding.headerNormal.setVisible(!status)
                        if (status) {
                            binding.deleteBtn.text = resources.getString(R.string.delete)
                            binding.deleteBtn.alpha = 0.4F
                        }
                    }
                }

                launch {
                    viewModel.selectAllEvent.collect {
                        adapter.selectAll()
                    }
                }
                launch {
                    viewModel.onFinishEvent.collect {
                        adapter.closeEdit()
                    }
                }
                launch {
                    viewModel.updatedBookEvent.collect {
                        binding.swipeRefresh.isRefreshing = false
                        if (it.isEmpty()) {
                            return@collect
                        }
                        requireContext().showShortToast("有 ${it.size} 本书有更新")
                        it.forEach {
                            adapter.setBookUpdate(it.first)
                        }
                    }
                }
            }
        }
    }
}
