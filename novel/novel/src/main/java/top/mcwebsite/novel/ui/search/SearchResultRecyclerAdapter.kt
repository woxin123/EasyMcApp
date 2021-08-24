package top.mcwebsite.novel.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.ItemSearchBookResultBinding
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.ui.search.SearchResultRecyclerAdapter.SearchResultRecyclerViewHolder

class SearchResultRecyclerAdapter(private val viewModel: SearchViewModel) :
    RecyclerView.Adapter<SearchResultRecyclerViewHolder>() {

    private val books = mutableListOf<BookModel>()

    fun setData(books: List<BookModel>) {
        this.books.clear()
        this.books.addAll(books)
        notifyDataSetChanged()
    }

    fun updateBookItem(book: BookModel, position: Int) {
        books[position] = book
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultRecyclerViewHolder {
        return SearchResultRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchResultRecyclerViewHolder, position: Int) {
        holder.bind(viewModel, books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    class SearchResultRecyclerViewHolder(private val binding: ItemSearchBookResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): SearchResultRecyclerViewHolder {
                return SearchResultRecyclerViewHolder(
                    ItemSearchBookResultBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

        fun bind(viewModel: SearchViewModel, book: BookModel) {
            binding.apply {
                bookCover.load(book.coverUrl) {
                    transformations(
                        RoundedCornersTransformation(
                            dip2px(
                                binding.root.context,
                                2F
                            ).toFloat()
                        )
                    )
                    placeholder(R.drawable.default_img_cover)
                    error(R.drawable.default_img_cover)
                    fallback(R.drawable.default_img_cover)
                }
                root.setOnClickListener {
                    viewModel.clickSearchItem(book)
                }
                bookName.text = book.name
                bookAuthor.text = book.author
                bookSource.text =
                    binding.root.context.getString(R.string.from_source, book.source)
                bookType.text = book.bookType
                bookLast.text = book.lastChapter
                if (book.atBookShelf) {
                    addToBookshelf.text = root.resources.getString(R.string.added)
                    addToBookshelf.setBackgroundColor(root.resources.getColor(R.color.gray))
                } else {
                    addToBookshelf.text = root.resources.getString(R.string.add)
                    addToBookshelf.setBackgroundColor(root.resources.getColor(R.color.colorPrimary))
                }
                addToBookshelf.setOnClickListener {
                    if (book.atBookShelf) {
                        return@setOnClickListener
                    }
                    viewModel.addToBookShelf(book, adapterPosition)
                }
            }
        }
    }


}