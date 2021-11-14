package top.mcwebsite.novel.ui.read

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.components.ImmersionFragment
import com.gyf.immersionbar.ktx.immersionBar
import com.gyf.immersionbar.ktx.showStatusBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.common.ui.utils.dp
import top.mcwebsite.common.ui.view.CircleView
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.config.ReadColor
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.config.readColors
import top.mcwebsite.novel.databinding.FragmentReadBookBinding
import top.mcwebsite.novel.ui.read.page.PageViewDrawer
import top.mcwebsite.novel.ui.read.view.PageWidget


class ReadBookFragment : ImmersionFragment(), KoinComponent {

    private val viewModel: ReadViewModel by viewModel()

    private lateinit var binding: FragmentReadBookBinding

    private lateinit var  pageViewDrawer: PageViewDrawer

    private val bookMenuAdapter: BookMenuAdapter by lazy {  BookMenuAdapter(viewModel) }

    private val readConfig: ReadConfig by inject()

    private var lastBatteryLevel = -1

    private val readConfigSettingsFragment: ReadConfigSettingsFragment by lazy {
        ReadConfigSettingsFragment().apply {
            onReadSettingChangeListener = object : ReadConfigSettingsFragment.OnReadSettingChangeListener {
                override fun onColorChange(readColor: ReadColor) {
                    binding.page.updateColor()
                }

                override fun onTextChange() {
                    binding.page.updateTextSize()
                }

                override fun onBrightnessChange(brightness: Float) {
                    readConfig.brightness = brightness
                    setCurrentPageBrightness(brightness)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                quit()
                findNavController().navigateUp()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_read_book, container, false)
        binding = FragmentReadBookBinding.bind(view)

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
        setCurrentPageBrightness(readConfig.brightness)
        binding.apply {
            bookName.text = viewModel.bookEntity.name

            // 初始化 menu recycler view
            bookMenu.adapter = bookMenuAdapter
            bookMenu.layoutManager = CenterLinearLayoutManager(requireContext())
            page.touchListener = object : PageWidget.TouchListener {
                override fun center() {
                    viewModel.changeMenuStatus(true)

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
            pageViewDrawer = PageViewDrawer()
            pageViewDrawer.pageWidget = binding.page
            pageViewDrawer.pageWidget = page
            page.pageDrawer = pageViewDrawer
            viewModel.pageProvider.pageDrawer = pageViewDrawer
            pageViewDrawer.pageProvider = viewModel.pageProvider
            centerArea.setOnClickListener {
                viewModel.changeMenuStatus(false)
            }

            bookMenuTv.setOnClickListener {
                viewModel.changeBookMenuStatus(true)
            }

            readSettings.setOnClickListener {
                viewModel.changeMenuStatus(false)
                readConfigSettingsFragment.show(childFragmentManager, null)
            }

            // 禁止侧滑栏手动启动
            root.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            // 返回键可以返回
            root.isFocusableInTouchMode = false
            root.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerClosed(drawerView: View) {
                    viewModel.changeBookMenuStatus(false)
                }
            })
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
                        val progress = (viewModel.bookEntity.lastReadChapterPos * 1F / it.size) * 100
                        binding.seekBar.progress = progress.toInt()
                    }
                }

                launch {
                    viewModel.pageProvider.curChapterLoadedEvent.collect {
                        if (it == viewModel.pageProvider.chapterPos) {
                            binding.page.drawCurPage(false)
                            binding.page.postInvalidate()
                        }
                    }
                }

                launch {
                    viewModel.drawReadPageEvent.collect {
                        binding.page.drawCurPage(false)
                        binding.page.postInvalidate()
                    }
                }

                launch {
                    viewModel.openChapter.collect {
                        bookMenuAdapter.updateChapterMenuSelect(it)
                    }
                }

                launch {
                    viewModel.menuStatus.collect {
                        if (it) {
                            binding.readMenuLayout.visibility = View.VISIBLE
                            showBar()
                        } else {
                            binding.readMenuLayout.visibility = View.GONE
                            hideBar()
                        }
                    }
                }

                launch {
                    viewModel.bookMenuStatus.collect {
                        if (it) {
                            (binding.bookMenu.layoutManager as LinearLayoutManager)
                                .scrollToPositionWithOffset(viewModel.pageProvider.chapterPos, binding.bookMenu.height / 3)
                            binding.root.openDrawer(GravityCompat.START)
                            bookMenuAdapter.updateChapterMenuSelect(viewModel.pageProvider.chapterPos)
                        }
                    }
                }

                launch {
                    viewModel.pageProvider.chapterPosChangeEvent.collect {
                        binding.seekBar
                        val progress = (it * 1F / viewModel.chapters.size) * 100
                        binding.seekBar.progress = progress.toInt()
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
                        if (lastBatteryLevel != batteryCapacity) {
                            pageViewDrawer.updateBattery(batteryCapacity)
                            lastBatteryLevel = batteryCapacity
                        }
                    }
                    Intent.ACTION_TIME_TICK -> pageViewDrawer.updateTime()
                }
            }

        }, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateBookEntity()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setCurrentPageBrightness(brightness: Float) {
        val lp = requireActivity().window.attributes
        lp.screenBrightness = brightness
        requireActivity().window.attributes = lp
    }

    override fun initImmersionBar() {
        hideBar()
    }

    private fun hideBar() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_BAR)
            fitsSystemWindows(false)
        }
    }

    override fun onVisible() {
        super.onVisible()
        if (viewModel.menuStatus.value) {
            showBar()
        } else {
            hideBar()
        }
    }

    override fun onInvisible() {
        super.onInvisible()
        immersionBar {
            hideBar(BarHide.FLAG_SHOW_BAR)
            statusBarColor(R.color.colorPrimary)
            removeSupportAllView()
            // 先置为 false 切换到默认状态
            fitsSystemWindows(false)
            // 再设置为 true
            fitsSystemWindows(true)
        }
    }

    private fun showBar() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            showStatusBar()
            titleBarMarginTop(binding.readMenuLayout)
            statusBarColor(R.color.colorPrimary)
        }
    }

    private fun quit() {
        resetScreenBrightness()
    }

    private fun resetScreenBrightness() {
        setCurrentPageBrightness(-1F)
    }

}