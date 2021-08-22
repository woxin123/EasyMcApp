package top.mcwebsite.novel.ui.bookshelf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.novel.databinding.ItemBookShelfBinding
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.ui.bookshelf.BookShelfRecyclerAdapter.BookShelfRecyclerViewHolder

class BookShelfRecyclerAdapter : RecyclerView.Adapter<BookShelfRecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfRecyclerViewHolder {
        return BookShelfRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookShelfRecyclerViewHolder, position: Int) {
        holder.bind(
            BookModel(
                "龙族",
                author = "江南",
                coverUrl = "https://www.75xs.cc/d/file/book/aefbfeac075ff32bc9523bab1a878ad2.jpg",
                source = "mock",
                url = "xxx"
            )
        )
    }

    override fun getItemCount(): Int {
        return 19
    }


    class BookShelfRecyclerViewHolder(private val binding: ItemBookShelfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookModel) {
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
            binding.bookProgress.text = "未读"
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