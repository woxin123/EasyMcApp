package top.mcwebsite.novel.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.novel.databinding.ItemRecentSearchBinding
import top.mcwebsite.novel.ui.search.RecentSearchRecyclerAdapter.*

class RecentSearchRecyclerAdapter(private val viewModel: SearchViewModel) : RecyclerView.Adapter<RecentSearchRecyclerViewHolder>() {

    private var _data = emptyList<String>()

    fun setData(list: List<String>) {
        _data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentSearchRecyclerViewHolder {
        return RecentSearchRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecentSearchRecyclerViewHolder, position: Int) {
        holder.bind(viewModel, _data[position])
    }

    override fun getItemCount(): Int {
        return _data.size
    }

    class RecentSearchRecyclerViewHolder(private val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup) : RecentSearchRecyclerViewHolder {
                return RecentSearchRecyclerViewHolder(
                    ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }

        fun bind(viewModel: SearchViewModel, text: String) {
            binding.viewModel = viewModel
            binding.searchText = text
        }
    }


}