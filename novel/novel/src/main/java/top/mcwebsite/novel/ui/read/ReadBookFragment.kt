package top.mcwebsite.novel.ui.read

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.databinding.FragmentReadBookBinding
import top.mcwebsite.novel.ui.read.view.PageViewDrawer
import top.mcwebsite.novel.ui.read.view.PageWidget


class ReadBookFragment : Fragment(), KoinComponent {

    private val viewModel: ReadViewModel by viewModel()

    private lateinit var binding: FragmentReadBookBinding

    private val pageViewDrawer: PageViewDrawer by inject()

    private val bookMenuAdapter: BookMenuAdapter by lazy {  BookMenuAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_read_book, container, false)
        binding = FragmentReadBookBinding.bind(view)
        pageViewDrawer.pageWidget = binding.page

        arguments?.let {
            viewModel.setBook(it.getParcelable(Constant.BOOK_ENTITY)!!)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservable()
    }

    private fun initView() {
        binding.apply {
            bookName.text = viewModel.bookEntity.name

            // 初始化 menu recycler view
            bookMenu.adapter = bookMenuAdapter
            bookMenu.layoutManager = LinearLayoutManager(requireContext())
            page.touchListener = object : PageWidget.TouchListener {
                override fun center() {
                    Toast.makeText(requireContext(), "点击了中间区域", Toast.LENGTH_SHORT).show()
                }

                override fun prePage(): Boolean {
                    return true
                }

                override fun nextPage(): Boolean {
                    return true
                }

                override fun cancel() {
                    Toast.makeText(requireContext(), "cancel", Toast.LENGTH_SHORT).show()
                }

            }
            pageViewDrawer.pageWidget = page
            page.pageDrawer = pageViewDrawer
            page.drawCurPage(false)

        }
    }

    private fun initObservable() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.chapterList.collect {
                        binding.totalChapter.text = "共 ${it.size + 1} 章"
                        bookMenuAdapter.setBookMenu(it)
                        pageViewDrawer.pageProvider = viewModel.pageProvider
                        pageViewDrawer.status = PageViewDrawer.STATUS_LOADING
                        launch {
                            viewModel.pageProvider.curChapterLoadedEvent.collect {
                                Log.d("mengchen", "第 $it 章节加载好了")
                                if (it == viewModel.pageProvider.chapterPos) {
                                    Log.d("mengchen", "第 $it 章节需要重新绘制")
                                    binding.page.drawCurPage(false)
                                    binding.page.postInvalidate()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fullScreen() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            fitsSystemWindows(true)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateBookEntity()
    }

}