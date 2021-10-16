package top.mcwebsite.novel.ui.read

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.novel.R
import top.mcwebsite.novel.data.local.db.entity.ChapterEntity
import top.mcwebsite.novel.databinding.ItemBookMenuBinding
import top.mcwebsite.novel.model.Chapter
import kotlin.jvm.internal.Intrinsics

class BookMenuAdapter(private val viewModel: ReadViewModel) :
    RecyclerView.Adapter<BookMenuAdapter.BookMenuViewHolder>() {

    private val data = mutableListOf<ChapterEntity>()

    private var currentSelected: Int = -1


    @SuppressLint("NotifyDataSetChanged")
    fun setBookMenu(chapters: List<ChapterEntity>) {
        data.clear()
        data.addAll(chapters)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMenuViewHolder {
        return BookMenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BookMenuViewHolder, position: Int) {
        holder.bind(data[position], viewModel, currentSelected)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateChapterMenuSelect(chapterIndex: Int) {
        val lastSelected = currentSelected
        currentSelected = chapterIndex
        if (lastSelected > 0) {
            notifyItemChanged(lastSelected)
        }
        notifyItemChanged(currentSelected)
    }

    class BookMenuViewHolder(private val binding: ItemBookMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BookMenuViewHolder {
                return BookMenuViewHolder(
                    ItemBookMenuBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        fun bind(chapter: ChapterEntity, viewModel: ReadViewModel, selectedIndex: Int) {
            binding.apply {
                title.text = chapter.title
                if (selectedIndex == adapterPosition) {
                    title.setTextColor(itemView.context.getColor(R.color.colorPrimary))
                } else {
                    title.setTextColor(itemView.context.getColor(R.color.black))
                }
                root.setOnClickListener {
                    viewModel.openChapter(adapterPosition)
                }
            }
        }

    }

}