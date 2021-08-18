package top.mcwebsite.novel.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.novel.databinding.ItemSearchResultBinding
import top.mcwebsite.novel.ui.search.SearchResultRecyclerAdapter.SearchResultRecyclerViewHolder

class SearchResultRecyclerAdapter : RecyclerView.Adapter<SearchResultRecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultRecyclerViewHolder {
        return SearchResultRecyclerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchResultRecyclerViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

    class SearchResultRecyclerViewHolder(binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): SearchResultRecyclerViewHolder {
                return SearchResultRecyclerViewHolder(
                    ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }


}