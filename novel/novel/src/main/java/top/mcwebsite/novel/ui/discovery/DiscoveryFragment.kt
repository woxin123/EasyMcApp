package top.mcwebsite.novel.ui.discovery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentDiscoveryBinding

class DiscoveryFragment : Fragment() {

    private val viewModel: DiscoveryViewModel by viewModel()

    private lateinit var binding: FragmentDiscoveryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false).also {
            binding = FragmentDiscoveryBinding.bind(it)
            binding.lifecycleOwner = this@DiscoveryFragment.viewLifecycleOwner
            binding.viewModel = viewModel
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchContent.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_discoveryFragment_to_searchFragment)
            }
            // 注意如果这里返回 true，那么 onClick 就不会被调用
            false
        }
    }
}
