package top.mcwebsite.novel.ui.bookshelf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.databinding.ItemBookShelfBinding
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.ui.bookshelf.BookShelfRecyclerAdapter.BookShelfRecyclerViewHolder

class BookShelfRecyclerAdapter(private val viewModel: BookshelfViewModel) : RecyclerView.Adapter<BookShelfRecyclerViewHolder>() {

    private val data = mutableListOf<BookEntity>()

    fun setBooks(books: List<BookEntity>) {
        data.clear()
        data.addAll(books)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfRecyclerViewHolder {
        return BookShelfRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookShelfRecyclerViewHolder, position: Int) {
        holder.bind(viewModel, data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class BookShelfRecyclerViewHolder(private val binding: ItemBookShelfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: BookshelfViewModel, book: BookEntity) {
            binding.bookName.text = book.name
            binding.bookCover.load(book.coverUrl) {
                transformations(
                    RoundedCornersTransformation(
                        dip2px(
                            binding.root.context,
                            2F
                        ).toFloat()
                    )
                )
            }
            if (book.lastReadChapterPos == -1) {
                binding.bookProgress.text = "未读"
            } else {
                binding.bookProgress.text = book.lastReadChapterTitle
            }
            binding.root.setOnClickListener {
                viewModel.clickItem(book)
            }
        }

        companion object {
            fun from(parent: ViewGroup): BookShelfRecyclerViewHolder {
                return BookShelfRecyclerViewHolder(
                    ItemBookShelfBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

    }

}