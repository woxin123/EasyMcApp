package top.mcwebsite.novel.ui.read

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.databinding.ItemBookMenuBinding
import top.mcwebsite.novel.model.Chapter

class BookMenuAdapter(private val viewModel: ReadViewModel) : RecyclerView.Adapter<BookMenuAdapter.BookMenuViewHolder>() {

    private val data = mutableListOf<ChapterEntity>()

    fun setBookMenu(chapters: List<ChapterEntity>) {
        data.clear()
        data.addAll(chapters)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMenuViewHolder {
        return BookMenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookMenuViewHolder, position: Int) {
        holder.bind(data[position], viewModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class BookMenuViewHolder(private val binding: ItemBookMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BookMenuViewHolder {
                return BookMenuViewHolder(ItemBookMenuBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ))
            }
        }

        fun bind(chapter: ChapterEntity, viewModel: ReadViewModel) {
            binding.apply {
                title.text = chapter.title
                root.setOnClickListener {
                    viewModel.openChapter(adapterPosition)
                }
            }
        }

    }

}