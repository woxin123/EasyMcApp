package top.mcwebsite.novel.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import top.mcwebsite.common.ui.loading.LoadingDialog
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentNovelHomeBinding
import top.mcwebsite.novel.ui.bookshelf.BookShelfFragment
import top.mcwebsite.novel.ui.discovery.DiscoveryFragment
import top.mcwebsite.novel.ui.me.MeFragment
import top.mcwebsite.novel.ui.rank.RankFragment

class NovelHomeFragment : Fragment() {

    lateinit var fragmentNovelHomeBinding: FragmentNovelHomeBinding

    private val bookShelfFragment by lazy { BookShelfFragment() }

    private val discoveryFragment by lazy { DiscoveryFragment() }

    private val rankFragment by lazy { RankFragment() }

    private val meFragment by lazy { MeFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // TODO 探究 attachToRoot true false 区别
        return inflater.inflate(R.layout.fragment_novel_home, container, false).also {
            fragmentNovelHomeBinding = FragmentNovelHomeBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = listOf(bookShelfFragment, discoveryFragment, rankFragment, meFragment)
        fragmentNovelHomeBinding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        fragmentNovelHomeBinding.bottomTab.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.book_shelf -> {
                    fragmentNovelHomeBinding.viewPager.currentItem = 0
                }
                R.id.discovery -> {
                    fragmentNovelHomeBinding.viewPager.currentItem = 1
                }
                R.id.rank -> {
                    fragmentNovelHomeBinding.viewPager.currentItem = 2
                }
                R.id.me -> {
                    fragmentNovelHomeBinding.viewPager.currentItem = 3
                }
            }
            true
        }
    }

}