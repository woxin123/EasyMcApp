package top.mcwebsite.novel.ui.rank

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.common.ui.utils.getColor
import top.mcwebsite.novel.R
import top.mcwebsite.novel.databinding.ItemRankCategoryBinding
import top.mcwebsite.novel.ui.rank.RankCategoryAdapter.RankCategoryViewHolder

class RankCategoryAdapter(
    private val categoryNames: List<String>,
    private val onItemClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<RankCategoryViewHolder>() {

    private var selectedIndex = 0

    class RankCategoryViewHolder(private val binding: ItemRankCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): RankCategoryViewHolder {
                return RankCategoryViewHolder(
                    ItemRankCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

        fun bind(name: String, selected: Boolean) {
            binding.rankCategoryName.text = name
            if (selected) {
                binding.rankCategoryName.setTextColor(binding.getColor(R.color.colorPrimary))
            } else {
                binding.rankCategoryName.setTextColor(binding.getColor(R.color.black))
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankCategoryViewHolder {
        return RankCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: RankCategoryViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(categoryNames[position], selectedIndex == position)
        holder.itemView.setOnClickListener {
            if (position == selectedIndex) {
                return@setOnClickListener
            }
            onItemClickListener.invoke(position)
            val lastSelectedIndex = selectedIndex
            selectedIndex = position
            notifyItemChanged(lastSelectedIndex)
            notifyItemChanged(selectedIndex)
        }
    }

    override fun getItemCount(): Int {
        return categoryNames.size
    }
}