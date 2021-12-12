package top.mcwebsite.novel.ui.rank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.ItemRankBookBinding
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.ui.rank.BookRankAdapter.BookRankViewHolder

class BookRankAdapter(
    private var data: List<BookModel>,
    private val onItemClickListener: (BookModel) -> Unit
) : RecyclerView.Adapter<BookRankViewHolder>() {


    class BookRankViewHolder(private val binding: ItemRankBookBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): BookRankViewHolder {
                return BookRankViewHolder(ItemRankBookBinding.inflate(
                    LayoutInflater.from(parent.context)
                ))
            }
        }

        fun bind(bookModel: BookModel) {
            binding.bookCover.load(bookModel.coverUrl) {
                placeholder(R.drawable.default_img_cover)
                error(R.drawable.default_img_cover)
                fallback(R.drawable.default_img_cover)
            }
            binding.bookName.text = bookModel.name
            binding.author.text = bookModel.author
        }
    }

    fun refreshData(data: List<BookModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookRankViewHolder {
        return BookRankViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookRankViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}