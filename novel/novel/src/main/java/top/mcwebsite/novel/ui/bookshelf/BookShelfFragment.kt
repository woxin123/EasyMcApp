package top.mcwebsite.novel.ui.bookshelf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.FragmentBookShelfBinding

class BookShelfFragment : Fragment() {

    private lateinit var fragmentBookShelfBinding: FragmentBookShelfBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBookShelfBinding = FragmentBookShelfBinding.inflate(inflater, container, false)
        return fragmentBookShelfBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 第一步，初始化 RecycleView
        fragmentBookShelfBinding.recyclerview.let { recyclerView ->
            recyclerView.adapter = BookShelfRecyclerAdapter()
            recyclerView.layoutManager = GridLayoutManager(activity, 3)
        }
    }
}