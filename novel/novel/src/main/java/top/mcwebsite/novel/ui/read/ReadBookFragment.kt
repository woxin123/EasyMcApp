package top.mcwebsite.novel.ui.read

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.immersionBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.data.local.db.entity.BookEntity


class ReadBookFragment : Fragment() {

    private val viewModel: ReadViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        arguments?.let {
            viewModel.bokEntity = it.getParcelable(Constant.BOOK_ENTITY)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read_book, container, false)
    }

    private fun fullScreen() {
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            fitsSystemWindows(true)
        }
    }


}