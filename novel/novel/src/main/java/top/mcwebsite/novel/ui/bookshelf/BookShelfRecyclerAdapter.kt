package top.mcwebsite.novel.ui.bookshelf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import top.mcwebsite.common.android.ext.setVisible
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.novel.data.local.db.entity.BookEntity
import top.mcwebsite.novel.databinding.ItemBookShelfBinding
import top.mcwebsite.novel.ui.bookshelf.BookShelfRecyclerAdapter.BookShelfRecyclerViewHolder

class BookShelfRecyclerAdapter(private val viewModel: BookshelfViewModel) : RecyclerView.Adapter<BookShelfRecyclerViewHolder>() {

    private val data = mutableListOf<BookEntity>()
    private var selectStatus : BooleanArray = BooleanArray(0)

    private var isEditing = false

    fun setBooks(books: List<BookEntity>) {
        data.clear()
        data.addAll(books)
        selectStatus = BooleanArray(books.size) { false }
        notifyDataSetChanged()
    }

    fun selectAll() {
        for (index in selectStatus.indices) {
            if (!selectStatus[index]) {
                selectStatus[index] = true
                notifyItemChanged(index)
                viewModel.onBookSelected(true, data[index])
            }
        }
    }

    fun setBookUpdate(index: Int) {
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfRecyclerViewHolder {
        return BookShelfRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookShelfRecyclerViewHolder, position: Int) {
        holder.bind(
            viewModel,
            data[position],
            isEditing, selectStatus[position],
            onCheckedBoxClick = { isChecked ->
                selectStatus[position] = isChecked
            },
        )
        holder.itemView.setOnLongClickListener {
            isEditing = true
            viewModel.changeEditStus(isEditing)
            notifyItemRangeChanged(0, data.size)
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun closeEdit() {
        if (isEditing) {
            isEditing = false
            for (index in selectStatus.indices) {
                if (selectStatus[index]) {
                    selectStatus[index] = false
                    viewModel.onBookSelected(false, data[index])
                }
            }
            notifyItemRangeChanged(0, data.size)
            viewModel.changeEditStus(false)
        }
    }


     class BookShelfRecyclerViewHolder(private val binding: ItemBookShelfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            viewModel: BookshelfViewModel,
            book: BookEntity,
            isEditing: Boolean,
            isSelected: Boolean,
            onCheckedBoxClick: (isChecked: Boolean) -> Unit,
        ) {
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
            if (isEditing) {
                binding.checkbox.visibility = View.VISIBLE
                binding.checkbox.isChecked = isSelected
            } else {
                binding.checkbox.visibility = View.GONE
                binding.checkedBackground.visibility = View.GONE
            }

            binding.updateTv.setVisible(book.isUpdate)

            binding.root.setOnClickListener {
                if (!isEditing) {
                    viewModel.clickItem(book)
                }
            }
            binding.checkbox.setOnClickListener {
                binding.checkedBackground.setVisible(binding.checkbox.isChecked)
                onCheckedBoxClick.invoke(binding.checkbox.isChecked)
                viewModel.onBookSelected(binding.checkbox.isChecked, book)
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