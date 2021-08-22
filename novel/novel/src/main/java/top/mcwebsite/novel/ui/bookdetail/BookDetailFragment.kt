package top.mcwebsite.novel.ui.bookdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.novel.R
import top.mcwebsite.novel.common.Constant
import top.mcwebsite.novel.databinding.FragmentBookDetailBinding
import top.mcwebsite.novel.model.BookModel


class BookDetailFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailBinding

    private val viewModel: BookDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val bookModel = it.getParcelable<BookModel>(Constant.BOOK_MODEL)!!
            viewModel.book = bookModel
            Toast.makeText(this.requireContext(), bookModel.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false).also {
            binding = FragmentBookDetailBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.appBar.title = viewModel.book.name
        binding.appBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.appBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


}