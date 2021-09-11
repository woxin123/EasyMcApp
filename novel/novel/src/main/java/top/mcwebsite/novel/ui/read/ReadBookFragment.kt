package top.mcwebsite.novel.ui.read

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.databinding.FragmentReadBookBinding
import top.mcwebsite.novel.ui.read.view.PageViewDrawer
import top.mcwebsite.novel.ui.read.view.PageWidget


class ReadBookFragment : Fragment(), KoinComponent {

    private val viewModel: ReadViewModel by viewModel()

    private lateinit var binding: FragmentReadBookBinding

    private val pageViewDrawer: PageViewDrawer by inject()

    private val bookMenuAdapter: BookMenuAdapter by lazy {  BookMenuAdapter(viewModel) }

    private val readConfig: ReadConfig by inject()

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
        initData()
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


        }
        if (Build.VERSION.SDK_INT >= 28) {
            activity?.window?.attributes?.let {
                it.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                activity?.window?.attributes = it
                binding.page.setOnApplyWindowInsetsListener { _, insets ->
                    val displayCutout = insets.displayCutout
                    if (displayCutout != null) {
                        val top = displayCutout.safeInsetTop
                        pageViewDrawer.safeInsetTop = top
                        binding.page.postInvalidate()
                    }
                    insets.consumeSystemWindowInsets()
                }
            }

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

                launch {
                    viewModel.drawReadPageEvent.collect {
                        binding.page.drawCurPage(false)
                        binding.page.postInvalidate()
                    }
                }
            }
        }
    }

    private fun initData() {
        registerBroadcast()
    }

    /**
     * 注册所需得广播
     */
    private fun registerBroadcast() {
        val intentFilter = IntentFilter().apply {
            // 注册电池电量变化得广播
            addAction(Intent.ACTION_BATTERY_CHANGED)
            // 注册时间变化得广播
            addAction(Intent.ACTION_TIME_TICK)
        }
        activity?.registerReceiver(object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_BATTERY_CHANGED -> {
                        val batteryCapacity = intent.getIntExtra("level", 0)
                        pageViewDrawer.updateBattery(batteryCapacity)
                    }
                    Intent.ACTION_TIME_TICK -> pageViewDrawer.updateTime()
                }
            }

        }, intentFilter)
    }

    private fun fullScreen() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateBookEntity()
    }

}