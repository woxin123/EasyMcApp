package top.mcwebsite.novel.ui.bookdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.common.ui.utils.dip2px
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
            viewModel.setBookModel(bookModel)
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
        initObservable()
    }

    private fun initView() {
        updateBookView()
        binding.title.text = viewModel.book.name
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateBookView() {
        binding.apply {
            val book = viewModel.book
            bookName.text = book.name
            if (!book.coverUrl.isNullOrBlank()) {
                bookCover.load(book.coverUrl) {
                    transformations(RoundedCornersTransformation(dip2px(requireContext(), 2F).toFloat()))
                    addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.78")
                    error(R.drawable.default_img_cover)
                }
            }
            bookAuthor.text = book.author
            bookSource.text = resources.getString(R.string.from_source, book.source)
            bookType.text = book.bookType
            bookLast.text = book.lastChapter
            if (book.introduce.isBlank()) {
                bookIntroduceContent.text = resources.getString(R.string.loading)
            } else {
                bookIntroduceContent.text = book.introduce
            }
            if (!book.update.isNullOrBlank()) {
                updateTime.text = resources.getString(R.string.update_time, book.update)
            }
        }
    }

    private fun initObservable() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateBookModel.collect { updateBookView() }
            }
        }
    }


}