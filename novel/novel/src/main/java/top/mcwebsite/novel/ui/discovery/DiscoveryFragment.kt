package top.mcwebsite.novel.ui.discovery

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentDiscoveryBinding
import androidx.core.content.ContextCompat.getSystemService




class DiscoveryFragment : Fragment() {

    private val discoveryViewModel: DiscoveryViewModel by viewModel()

    private val imm by lazy {
        getSystemService(this.requireContext(), InputMethodManager::class.java)
    }

    private lateinit var viewBinding: FragmentDiscoveryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false).also {
            viewBinding = FragmentDiscoveryBinding.bind(it)
            viewBinding.lifecycleOwner = this@DiscoveryFragment
            viewBinding.viewModel = discoveryViewModel
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discoveryViewModel.searchContent.observe(this.viewLifecycleOwner) {
            Log.d("mengchen", "search content = $it")
        }
        discoveryViewModel.backVisibleState.observe(this.viewLifecycleOwner) {
            Log.d("mengchen", "back visible = $it")
        }
        viewBinding.searchContent.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                discoveryViewModel.showOrHideSearchView(true)
            }
            // 注意如果这里返回 true，那么 onClick 就不会被调用
            false
        }

        discoveryViewModel.backEvent.observe(this.viewLifecycleOwner) {
            viewBinding.searchContent.let {
                it.clearFocus()
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }
}